# Chapter 9: Fluid Pipes, Pump Covers & Liquid Transport

IC2 Rebranded includes a modular, high-throughput fluid pipe transport network designed for liquid automation, reactor cooling, and machine integration.

---

## 1. Overview of Fluid Pipes

IC2 fluid pipes come in two alloy types (**Bronze** and **Steel**) and four standardized diameters (**Tiny**, **Small**, **Medium**, and **Large**). The pipe's throughput scales according to both its material and its size diameter.

### Pipe Materials

| Material | Base Flow Rate | Description |
|---|---|---|
| **Bronze** | $2,400\text{ mB/s}$ ($120\text{ mB/t}$) | Standard alloy fluid pipe for base automation and fuel routing. |
| **Steel** | $4,800\text{ mB/s}$ ($240\text{ mB/t}$) | Heavy-duty, reinforced conduit with double the flow rate of bronze, ideal for nuclear reactor coolant circuits. |

### Pipe Sizes & Throughput Multipliers

The effective throughput per pipe is calculated as:
$$\text{Throughput} = \text{Base Material Rate} \times \text{Size Multiplier}$$

| Pipe Size | Block Registry Name | Thickness | Size Multiplier | Bronze Flow Rate | Steel Flow Rate |
|---|---|:---:|:---:|:---:|:---:|
| **Tiny** | `ic2:bronze_pipe_tiny`<br>`ic2:steel_pipe_tiny` | $4\text{ px}$ | $\times \frac{1}{6}$ | $20\text{ mB/t}$ ($400\text{ mB/s}$) | $40\text{ mB/t}$ ($800\text{ mB/s}$) |
| **Small** | `ic2:bronze_pipe_small`<br>`ic2:steel_pipe_small` | $6\text{ px}$ | $\times \frac{1}{3}$ | $40\text{ mB/t}$ ($800\text{ mB/s}$) | $80\text{ mB/t}$ ($1,600\text{ mB/s}$) |
| **Medium** | `ic2:bronze_pipe_medium`<br>`ic2:steel_pipe_medium` | $8\text{ px}$ | $\times 1.0$ | $120\text{ mB/t}$ ($2,400\text{ mB/s}$) | $240\text{ mB/t}$ ($4,800\text{ mB/s}$) |
| **Large** | `ic2:bronze_pipe_large`<br>`ic2:steel_pipe_large` | $10\text{ px}$ | $\times 2.0$ | $240\text{ mB/t}$ ($4,800\text{ mB/s}$) | $480\text{ mB/t}$ ($9,600\text{ mB/s}$) |

---

## 2. Pump Cover Attachments

Pipes by default transfer fluids passively along connected networks. To actively extract fluids from adjacent machines, tanks, or cauldrons, apply a **Pump Cover Attachment** to a pipe connection face.

### Pump Cover Tiers

Pump covers correspond to IC2 voltage tiers and provide increasingly rapid fluid extraction:

| Cover Tier | Item Registry Name | Extraction Rate | Color Accent | Recommended Use |
|---|---|:---:|:---:|---|
| **LV Pump Cover** | `ic2:cover_pump_lv` | $32\text{ mB/t}$ ($640\text{ mB/s}$) | Green | General machinery & basic fluid generators. |
| **MV Pump Cover** | `ic2:cover_pump_mv` | $128\text{ mB/t}$ ($2,560\text{ mB/s}$) | Yellow | High-demand ore washing & thermal centrifuges. |
| **HV Pump Cover** | `ic2:cover_pump_hv` | $512\text{ mB/t}$ ($10,240\text{ mB/s}$) | Orange | Nuclear reactor hot coolant extraction. |
| **EV Pump Cover** | `ic2:cover_pump_ev` | $2,048\text{ mB/t}$ ($40,960\text{ mB/s}$) | Red | Massive industrial fluid grids & matter generators. |

---

## 3. Cauldron Liquid Pumping

In modern Minecraft (1.21.1+), Pointed Dripstone can passively generate Water and Lava inside vanilla cauldrons. IC2 fluid pipes and pumps have native cauldron integration:

- **Fluid Pipes with Pump Covers**: Place any fluid pipe adjacent to or directly above a cauldron with a Pump Cover attached to that face. The pipe will automatically detect the cauldron fluid level and siphon the liquid directly into the pipe network as soon as it forms.
- **IC2 Machinery Pump**: An active IC2 Pump block placed directly in front of a cauldron (1-block distance) will automatically suck lava and water into its internal tank or connected fluid pipes.

---

## 4. Wrench Controls & Interactions

Fluid pipes and covers interact with any IC2 Wrench (`ItemToolWrench` or compatible wrenches):

- **Disconnect / Reconnect Face**: `Right-Click` on an open pipe arm with a wrench to toggle its connection status (open vs disconnected). This prevents pipes from accidentally bonding to neighboring blocks.
- **Retrieve Pump Cover**: `Sneak + Right-Click` on a covered pipe face with a wrench to safely detach the pump cover and drop it back into your inventory.
- **Harvest Pipe**: Right-clicking the center node of the pipe with an electric or standard wrench will safely dismantle the pipe and all attached covers.

---

## 5. Add-on Code: Programmatic Integration

Custom add-ons can interact with fluid pipes via NeoForge's `IFluidHandler` capability:

```java
import ic2.core.block.transport.PipeType;
import ic2.core.block.transport.PipeSize;
import ic2.core.block.transport.TransportPipeBlock;
import ic2.core.block.transport.TransportPipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class AddonPipeHelper {
    public static void inspectPipe(Level level, BlockPos pos, Direction side) {
        if (level.getBlockEntity(pos) instanceof TransportPipeBlockEntity pipe) {
            // Check pipe type and size
            PipeType type = pipe.getPipeType();
            PipeSize size = pipe.getPipeSize();
            int transferRate = type.getTransferRatePerTick();
            
            // Access fluid capability
            IFluidHandler fluidHandler = pipe.getFluidHandler(side);
        }
    }
}
```
