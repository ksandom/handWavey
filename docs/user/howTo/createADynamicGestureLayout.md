# How to create a dynamic gestureLayout

## What is a dynamic gestureLayout?

It's just like a [normal gestureLayout](createAGestureLayout.md), except that you can make some actions can change in a predictable way. Eg you can have a smaller number of easy-trigger gestures, but then be able to change the actions behind them like you'd change tools in a photo editor.

## How it works

* Instead of running explicit actions (eg `"keyDown("a");"`) when an event is triggered, you run a slot. (Eg `doSlot("0", "custom-0");` where `"0"` is the slot to be run, and `"custom-0"` is the event to trigger if the slot hasn't been set yet.)
* That slot can be redefined using a command like `setSlot("0", "custom-1");` where `"0"` is the slot number, and `"custom-1"` is the event to trigger.

The custom events can be configured without your gestureLayout just like you would for any other event within the gestureLayout.

## Commands

* `doSlot("slotNumber", "eventName")` - Runs the specified slot. Default to `"eventName"` if the slot hasn't been set.
* `setSlot("slotNumber", "eventName")` - Redefines the specified slot to use a specific eventName.
* `disableSlots()` - Disables the doSlot() function. Useful while the user is choosing a new toolset, and doesn't want to be taking action.
* `enableSlots()` - Re-enables the doSlot() function. Useful when the user has finished changing the toolset.
* `setAllSlots("")` - Sets all slots to the specified value. Setting this to `""` is particularly useful to go back to the gestureLayout's default assignments for all slots. Ie to return to the default state.

## Parameters

* `slotNumber` is an integery from 0 to 255.
* `eventName` is the name of the event to be triggered.

## A worked example

This is a copy and paste of the first few lines of the actionEvents.yml file from the tiltClick/actionModifier gestureLayout.

```yaml
groups: {}
items:
  general-zone-pAction-enter:
    value: disableSlots();
  general-zone-pAction-exit:
    value: enableSlots();
  individual-pAction0Open-enter:
    value: debug("0", "Toolset = Simple (default).");
  individual-pAction0Closed-enter:
    value: setAllSlots("");
  individual-pAction1Open-enter:
    value: debug("0", "Toolset = Copy/paste.");
  individual-pAction1Closed-enter:
    value: setAllSlots("");setSlot("0", "custom-101");setSlot("1", "custom-12");setSlot("3", "custom-11");
```

* The action zone is used to select toolsets.
* Rotating the hand denotes which toolset is desired.
* Closing the hand denotes selecting that toolset.

## Custom events

Custom events are what get triggered by the slots.

* These can all be changed or added to in actionEvents.yml in the user's config.
* Their default values are set in the generateCustomConfig function in [HandWaveyConfig.java](https://github.com/ksandom/handWavey/blob/main/src/main/java/handWavey/HandWaveyConfig.java).

These are the default actions for the custom events:

* Special
  * custom-0: Release keys and buttons. (Return state.)
  * custom-1:
  * custom-2:
  * custom-3:
  * custom-4:
  * custom-5:
  * custom-6:
  * custom-7:
  * custom-8:
  * custom-9:
* Basic clicks
  * custom-10: Mouse down - Left.
  * custom-11: Mouse down - Right.
  * custom-12: Mouse down - Middle.
  * custom-13:
  * custom-14:
  * custom-15:
  * custom-16:
  * custom-17:
  * custom-18:
  * custom-19:
* Multi clicks
  * custom-20: Double click hold.
  * custom-21: Tripple click hold.
  * custom-22: Double click.
  * custom-23: Tripple click.
  * custom-24:
  * custom-25:
  * custom-26:
  * custom-27:
  * custom-28:
  * custom-29:
* Modified clicks
  * custom-30: ALT + Left click.
  * custom-31: ALT + Right click.
  * custom-32: ALT + Middle click.
  * custom-33:
  * custom-34:
  * custom-35:
  * custom-36:
  * custom-37:
  * custom-38:
  * custom-39:
  * custom-40: CTRL + Left click.
  * custom-41: CTRL + Right click.
  * custom-42: CTRL + Middle click.
  * custom-43:
  * custom-44:
  * custom-45:
  * custom-46:
  * custom-47:
  * custom-48:
  * custom-49:
* Currently unassigned, but intended for modified clicks. Move there as needed.
  * custom-50:
  * custom-51:
  * custom-52:
  * custom-53:
  * custom-54:
  * custom-55:
  * custom-56:
  * custom-57:
  * custom-58:
  * custom-59:
  * custom-60:
  * custom-61:
  * custom-62:
  * custom-63:
  * custom-64:
  * custom-65:
  * custom-66:
  * custom-67:
  * custom-68:
  * custom-69:
  * custom-70:
  * custom-71:
  * custom-72:
  * custom-73:
  * custom-74:
  * custom-75:
  * custom-76:
  * custom-77:
  * custom-78:
  * custom-79:
  * custom-80:
  * custom-81:
  * custom-82:
  * custom-83:
  * custom-84:
  * custom-85:
  * custom-86:
  * custom-87:
  * custom-88:
  * custom-89:
  * custom-90:
  * custom-91:
  * custom-92:
  * custom-93:
  * custom-94:
  * custom-95:
  * custom-96:
  * custom-97:
  * custom-98:
  * custom-99:
* Keyboard shortcuts (No state-return needed.)
  * custom-100:
  * custom-101: CTRL + c
  * custom-102: CTRL + v
  * custom-103: CTRL + x
  * custom-104: Delete
  * custom-105: CTRL + z
  * custom-106: CTRL + SHIFT + z
* Currently unassigned, but intended for keyboard shortcuts. Move as needed.
  * custom-107:
  * custom-108:
  * custom-109:
  * custom-110:
  * custom-111:
  * custom-112:
  * custom-113:
  * custom-114:
  * custom-115:
  * custom-116:
  * custom-117:
  * custom-118:
  * custom-119:
  * custom-120:
  * custom-121:
  * custom-122:
  * custom-123:
  * custom-124:
  * custom-125:
  * custom-126:
  * custom-127:
  * custom-128:
  * custom-129:
  * custom-130:
  * custom-131:
  * custom-132:
  * custom-133:
  * custom-134:
  * custom-135:
  * custom-136:
  * custom-137:
  * custom-138:
  * custom-139:
  * custom-140:
  * custom-141:
  * custom-142:
  * custom-143:
  * custom-144:
  * custom-145:
  * custom-146:
  * custom-147:
  * custom-148:
  * custom-149:
  * custom-150:
  * custom-151:
  * custom-152:
  * custom-153:
  * custom-154:
  * custom-155:
  * custom-156:
  * custom-157:
  * custom-158:
  * custom-159:
  * custom-160:
  * custom-161:
  * custom-162:
  * custom-163:
  * custom-164:
  * custom-165:
  * custom-166:
  * custom-167:
  * custom-168:
  * custom-169:
  * custom-170:
  * custom-171:
  * custom-172:
  * custom-173:
  * custom-174:
  * custom-175:
  * custom-176:
  * custom-177:
  * custom-178:
  * custom-179:
* Scroll
  * custom-180: Scroll - Turn off.
  * custom-181: Scroll - Turn on.
  * custom-182: CTRL + Scroll - Turn on.
  * custom-183:
  * custom-184:
  * custom-185:
  * custom-186:
  * custom-187:
  * custom-188:
  * custom-189:
  * custom-190:
  * custom-191:
  * custom-192:
  * custom-193:
  * custom-194:
  * custom-195:
  * custom-196:
  * custom-197:
  * custom-198:
  * custom-199:
* Currently unassigned.
  * custom-200:
  * custom-201:
  * custom-202:
  * custom-203:
  * custom-204:
  * custom-205:
  * custom-206:
  * custom-207:
  * custom-208:
  * custom-209:
  * custom-210:
  * custom-211:
  * custom-212:
  * custom-213:
  * custom-214:
  * custom-215:
  * custom-216:
  * custom-217:
  * custom-218:
  * custom-219:
  * custom-220:
  * custom-221:
  * custom-222:
  * custom-223:
  * custom-224:
  * custom-225:
  * custom-226:
  * custom-227:
  * custom-228:
  * custom-229:
  * custom-230:
  * custom-231:
  * custom-232:
  * custom-233:
  * custom-234:
  * custom-235:
  * custom-236:
  * custom-237:
  * custom-238:
  * custom-239:
  * custom-240:
  * custom-241:
  * custom-242:
  * custom-243:
  * custom-244:
  * custom-245:
  * custom-246:
  * custom-247:
  * custom-248:
  * custom-249:
  * custom-250:
  * custom-251:
  * custom-252:
  * custom-253:
  * custom-254:
* Special
  * custom-255: Do nothing.

