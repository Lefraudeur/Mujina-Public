# Mujina
A cross-platform injectable cheat base for minecraft made in java, loaded with c++

One of the only cheats that works both on windows and linux.\
By default this cheat can be injected in minecraft fabric 1.20.4. It contains some example modules to showcase the different features this base provides.

This is more like a proof of concept right now, its main purpose is to show that you can make injectable cheats with java.\
There is a lot of useless and over-complicated code because it wasn't supposed to be public at first, and its part of a future bigger project.

Techniques used in this project should already be public knowledge (the jvm is documented really well) but somehow people still have no clue it exists.\
Before asking for support, refer to official jdk documentation, it will answer most of your questions.\
Here are all the ressources I used to code this project:
- https://docs.oracle.com/en/java/javase/22/
- https://docs.oracle.com/en/java/javase/22/docs/specs/jni/index.html
- https://docs.oracle.com/en/java/javase/22/docs/specs/jvmti.html
and for runtime bytecode editing as well as classfile parsing:
- https://docs.oracle.com/javase/specs/jvms/se22/html/index.html
- https://asm.ow2.io/asm4-guide.pdf


Following text is an attempt to make this beginner friendly (far from done),
You still need to read the 300000 page documentations at the moment.

## Basic Structure

### - Java Cheat :
The java project is located in `libMujina/InjectableJar/InjectableJar`, the folder has to be opened with Intellij.\
It contains the cheat's java code, most of the time you only have to edit that.\

Once you are done editing the code, use the proper build script for your system.\
In the building process, the jar file is first remapped and then converted to a C++ array so that it can be compiled into an injectable dll.

### - C++ Loader :
This part of the project takes the bytes of your jar file and embeds it into a dll.\
The dll will extract the .class files from your jar file using miniz and
each class will then be loaded using JNI defineClass method.

## Events / Existing Java code modification
This is the almost interesting part buut I'm too lazy to explain for now.

## Build Process :

Install ant and run the relative build script.

The result will be saved at `Build/Releases/libMujina.dll` or `Build/liblibMujina.so` if you are using linux.

## Mythology
Mujina takes inspiration from the Japanese Mythology, and incorporates it in to its symbolism. Mujina (also known as the Noppera-Bo), are faceless yõkai who seem like normal people from far away, but on closer inspection are faceless beings, who deeply unsettle and scare those who see them. Such is the user of the client - blends in and is faceless. 
 
## Developers
The Developers of the client are as follows:

LeFraudeur - Base, injection, and sdk, data transfer

Lagoon - Bypasses and CLI (and internal GUI and settings, apparently)

kek - sdk and data transfer/api between gui and client

rk3 - webview, external GUI, and website in general

CrystalEU aka Nullable - Bypasses and CLI 


## TODO

### base
- [X] Injection
- [x] Internal GUI
- [x] Settings :sob:
- [ ] CLI GUI
- [ ] WebGUI
- [x] Mappings

### Combat 
- [ ] Crystal Aura
- [ ] Anchor Aura 
- [ ] BedAura
- [ ] Anchor Macro
- [x] Triggerbot (lagoon)
- [x] AimAssist
- [x] WTap
- [x] NoMiss
- [x] Reach
- [ ] Hitboxes
- [x] Velocity 
- [x] JumpReset
- [x] Killaura
- [ ] Criticals
- [ ] AutoTotem
- [ ] AutoPot
- [ ] Refill

### Movement
- [x] Fly
- [x] Glide
- [x] Speed
- [ ] LongJump
- [ ] HighJump
- [ ] ClickTP
- [ ] StopMotion / Freeze
- [ ] SafeWalk
- [ ] NoSlow
- [x] VClip
- [x] Sprint
- [ ] InvMove

### Render
- [ ] ESP
- [x] ArrayList
- [ ] Search
- [ ] HitCircel
- [ ] HitAnimations
- [ ] World (worldtime, weather and allat)
- [ ] Obfuscate (username, chat, other peoples names, etc)
- [ ] Chams
- [ ] Nametags

### Player
- [x] AutoGG
- [ ] AutoEZ
- [x] FastPlace
- [x] NoFall
- [ ] Spammer
- [ ] Debug (packetlog, velocity, etc)

### World
- [x] Scaffold
- [ ] BridgeAssist
- [ ] AutoTool
- [ ] Fucker
- [ ] ChestStealer
- [x] FastBreak
- [ ] AntiVoid

### Misc
- [ ] AutoBedCover
- [ ] FakeLag
- [x] Blink
- [ ] AntiFireball
- [x] Teams
- [ ] AntiBot

### Exploit
- [x] Disabler
- [x] ServerCrasher
- [ ] Teleport
- [ ] TpAura(?) (too blatant + doesnt bypass?)
- [ ] DupeUtils

## Contributors
<a href="https://github.com/Lefraudeur/Mujina-Public/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Lefraudeur/Mujina-Public" />
</a>

Made with [contrib.rocks](https://contrib.rocks).
