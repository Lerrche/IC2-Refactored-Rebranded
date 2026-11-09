package ic2.core.gui;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ListValueSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import org.jetbrains.annotations.Nullable;

public class IC2ConfigSectionScreen extends ConfigurationSectionScreen {
  private static final int SEARCH_Y = 28;
  private static final int SEARCH_HEIGHT = 18;
  private static final int HEADER_HEIGHT = 56;

  private EditBox searchBox;
  private String searchQuery = "";

  public IC2ConfigSectionScreen(
      Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
    super(parent, type, modConfig, title, (c, k, e) -> e);
  }

  public IC2ConfigSectionScreen(
      Context parentContext,
      Screen parent,
      Map<String, Object> valueSpecs,
      String key,
      Set<? extends Entry> entrySet,
      Component title) {
    super(parentContext, parent, valueSpecs, key, entrySet, title);
  }

  @Override
  protected void addTitle() {
    this.layout.setHeaderHeight(HEADER_HEIGHT);
    this.layout.addToHeader(
        new StringWidget(this.title, this.font),
        settings -> settings.alignVerticallyTop().paddingTop(8));
  }

  @Override
  protected void repositionElements() {
    this.layout.setHeaderHeight(HEADER_HEIGHT);
    super.repositionElements();
    if (this.searchBox != null) {
      int searchWidth = Math.min(240, this.width - 60);
      this.searchBox.setX((this.width - searchWidth) / 2);
      this.searchBox.setY(SEARCH_Y);
      this.searchBox.setWidth(searchWidth);
      this.searchBox.setHeight(SEARCH_HEIGHT);
    }
  }

  @Override
  protected void init() {
    super.init();
    int searchWidth = Math.min(240, this.width - 60);
    int searchX = (this.width - searchWidth) / 2;
    this.searchBox =
        new EditBox(
            this.font,
            searchX,
            SEARCH_Y,
            searchWidth,
            SEARCH_HEIGHT,
            Component.translatable("ic2.configuration.search"));
    this.searchBox.setHint(Component.translatable("ic2.configuration.search_hint"));
    this.searchBox.setValue(this.searchQuery);
    this.searchBox.setResponder(this::onSearchChanged);
    this.addRenderableWidget(this.searchBox);
  }

  private void onSearchChanged(String text) {
    this.searchQuery = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
    this.rebuild();
  }

  @Override
  @Nullable protected Element createSection(
      final String key, final UnmodifiableConfig subconfig, final UnmodifiableConfig subsection) {
    if (subconfig.isEmpty()) return null;
    Component sectionLabel = getTranslationComponent(key);
    Component buttonLabel =
        Component.translatableWithFallback(
            getTranslationKey(key) + ".button",
            Component.translatableWithFallback("ic2.configuration.section.enter", "Open >")
                .getString());

    return new Element(
        sectionLabel,
        getTooltipComponent(key, null),
        Button.builder(
                buttonLabel,
                button ->
                    minecraft.setScreen(
                        sectionCache.computeIfAbsent(
                            key,
                            k ->
                                new IC2ConfigSectionScreen(
                                        context,
                                        this,
                                        subconfig.valueMap(),
                                        key,
                                        subsection.entrySet(),
                                        sectionLabel)
                                    .rebuild())))
            .tooltip(Tooltip.create(getTooltipComponent(key, null)))
            .width(Button.DEFAULT_WIDTH)
            .build());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected ConfigurationSectionScreen rebuild() {
    if (list != null) {
      list.children().clear();
      boolean hasUndoableElements = false;

      final List<@Nullable Element> elements = new ArrayList<>();
      for (final Entry entry : context.entries()) {
        final String key = entry.getKey();
        final Object rawValue = entry.getRawValue();
        switch (rawValue) {
          case ConfigValue cv -> {
            var valueSpec = getValueSpec(key);
            var element =
                switch (valueSpec) {
                  case ListValueSpec listValueSpec -> createList(key, listValueSpec, cv);
                  case ValueSpec spec
                      when cv.getClass() == ConfigValue.class
                          && spec.getDefault() instanceof String ->
                      createStringValue(key, valueSpec::test, () -> (String) cv.getRaw(), cv::set);
                  case ValueSpec spec
                      when cv.getClass() == ConfigValue.class
                          && spec.getDefault() instanceof Integer ->
                      createIntegerValue(key, valueSpec, () -> (Integer) cv.getRaw(), cv::set);
                  case ValueSpec spec
                      when cv.getClass() == ConfigValue.class
                          && spec.getDefault() instanceof Long ->
                      createLongValue(key, valueSpec, () -> (Long) cv.getRaw(), cv::set);
                  case ValueSpec spec
                      when cv.getClass() == ConfigValue.class
                          && spec.getDefault() instanceof Double ->
                      createDoubleValue(key, valueSpec, () -> (Double) cv.getRaw(), cv::set);
                  case ValueSpec spec
                      when cv.getClass() == ConfigValue.class
                          && spec.getDefault() instanceof Enum<?> ->
                      createEnumValue(key, valueSpec, (Supplier) cv::getRaw, (Consumer) cv::set);
                  case null -> null;

                  default ->
                      switch (cv) {
                        case ModConfigSpec.BooleanValue value ->
                            createBooleanValue(key, valueSpec, value::getRaw, value::set);
                        case ModConfigSpec.IntValue value ->
                            createIntegerValue(key, valueSpec, value::getRaw, value::set);
                        case ModConfigSpec.LongValue value ->
                            createLongValue(key, valueSpec, value::getRaw, value::set);
                        case ModConfigSpec.DoubleValue value ->
                            createDoubleValue(key, valueSpec, value::getRaw, value::set);
                        case ModConfigSpec.EnumValue value ->
                            createEnumValue(
                                key, valueSpec, (Supplier) value::getRaw, (Consumer) value::set);
                        default -> createOtherValue(key, cv);
                      };
                };
            if (matchesSearch(key, element, null)) {
              elements.add(context.filter().filterEntry(context, key, element));
            }
          }
          case UnmodifiableConfig subsection
              when context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig -> {
            Element sec = createSection(key, subconfig, subsection);
            if (matchesSearch(key, sec, subsection)) {
              elements.add(sec);
            }
          }
          default -> {
            Element other = createOtherSection(key, rawValue);
            if (matchesSearch(key, other, null)) {
              elements.add(context.filter().filterEntry(context, key, other));
            }
          }
        }
      }
      elements.addAll(createSyntheticValues());

      for (final Element element : elements) {
        if (element != null) {
          if (element.name() == null) {
            list.addSmall(
                new StringWidget(
                    Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty(), font),
                element.getWidget(options));
          } else {
            final StringWidget label =
                new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, element.name(), font)
                    .alignLeft();
            label.setTooltip(Tooltip.create(element.tooltip()));
            list.addSmall(label, element.getWidget(options));
          }
          hasUndoableElements |= element.undoable();
        }
      }

      if (hasUndoableElements && undoButton == null) {
        createUndoButton();
        createResetButton();
      }
    }
    return this;
  }

  private boolean matchesSearch(
      String key, @Nullable Element element, @Nullable UnmodifiableConfig subsection) {
    if (this.searchQuery == null || this.searchQuery.isBlank()) {
      return true;
    }
    if (key.toLowerCase(Locale.ROOT).contains(this.searchQuery)) {
      return true;
    }
    if (element != null) {
      if (element.name() != null
          && element.name().getString().toLowerCase(Locale.ROOT).contains(this.searchQuery)) {
        return true;
      }
      if (element.tooltip() != null
          && element.tooltip().getString().toLowerCase(Locale.ROOT).contains(this.searchQuery)) {
        return true;
      }
    }
    if (subsection != null && matchesSubsection(subsection, this.searchQuery)) {
      return true;
    }
    return false;
  }

  private static boolean matchesSubsection(UnmodifiableConfig section, String query) {
    for (Entry entry : section.entrySet()) {
      if (entry.getKey().toLowerCase(Locale.ROOT).contains(query)) {
        return true;
      }
      if (entry.getRawValue() instanceof UnmodifiableConfig child) {
        if (matchesSubsection(child, query)) {
          return true;
        }
      }
    }
    return false;
  }
}
