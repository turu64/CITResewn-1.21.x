# Minecraft 1.21.4 Migration Guide

Quick reference guide for developers working with the 1.21.4 compatibility implementation.

## Quick Start

This codebase uses **Stonecutter** for multi-version support. The same source code compiles for both pre-1.21.4 and 1.21.4+ versions using preprocessor directives.

## Stonecutter Syntax

### Basic Conditional

```java
/*? <1.21.4 {*/
// Code for Minecraft < 1.21.4
import net.minecraft.client.render.model.ModelLoader;
/*?} else {*/
/*// Code for Minecraft >= 1.21.4
import net.minecraft.client.render.model.BakedModelManager;
*//*?}*/
```

### Inline Conditional

```java
@Shadow @Final private /*? <1.21.4 {*/ModelLoader/*?} else {*//*BakedModelManager*//*?}*/ loader;
```

### Method Conditional

```java
/*? <1.21.4 {*/
public void oldImplementation() {
    // Pre-1.21.4 code
}
/*?} else {*/
/*public void newImplementation() {
    // 1.21.4+ code
}
*//*?}*/
```

## API Changes Quick Reference

| Old API (< 1.21.4) | New API (>= 1.21.4) | Status |
|-------------------|-------------------|--------|
| `ModelLoader` | `BakedModelManager` | ✅ Conditional |
| `ModelOverride` | _Removed_ | ✅ Alternative impl |
| `ModelOverrideList` | _Removed_ | ✅ Alternative impl |
| `ElytraItem` | _Removed_ | ✅ Use `Items.ELYTRA` |
| `ArmorMaterial.Layer` | `EquipmentAsset` | ✅ New mixin |
| `loadModelFromJson()` | _Changed_ | ⚠️ Disabled |

Legend:
- ✅ = Implemented
- ⚠️ = Partially implemented / needs work
- ❌ = Not implemented

## Common Patterns

### Pattern 1: Class Replacement

When a class is renamed or replaced:

```java
/*? <1.21.4 {*/
import net.minecraft.old.ClassName;
/*?} else {*/
/*import net.minecraft.new.ClassName;
*//*?}*/

/*? <1.21.4 {*/
@Mixin(OldClass.class)
/*?} else {*/
/*@Mixin(NewClass.class)*/
/*?}*/
public class MyMixin {
    // Implementation
}
```

### Pattern 2: Class Removal

When a class is removed entirely:

```java
/*? <1.21.4 {*/
import net.minecraft.item.ElytraItem;
/*?}*/

public void checkItem(Item item) {
    /*? <1.21.4 {*/
    if (item instanceof ElytraItem) {
        // Do something
    }
    /*?} else {*/
    /*if (item == Items.ELYTRA) {
        // Do something
    }
    *//*?}*/
}
```

### Pattern 3: Method Signature Change

When a method signature changes:

```java
/*? <1.21.4 {*/
public void oldMethod(OldType param) {
    // Old implementation
}
/*?} else {*/
/*public void newMethod(NewType param) {
    // New implementation
}
*//*?}*/
```

### Pattern 4: Wrapping Entire Features

When an entire feature needs different implementations:

```java
/*? <1.21.4 {*/
public class OldImplementation extends BaseClass {
    @Override
    public void feature() {
        // Old way
    }
}
/*?} else {*/
/*public class NewImplementation {
    // No inheritance, custom implementation
    public void feature() {
        // New way
    }
}
*//*?}*/
```

## Specific Implementations

### TypeItem.java - CITOverrideList

**Problem**: `ModelOverrideList` removed in 1.21.4

**Solution**: Custom implementation without inheritance

```java
/*? <1.21.4 {*/
public static class CITOverrideList extends ModelOverrideList {
    // Extends vanilla class
}
/*?} else {*/
/*public static class CITOverrideList {
    // Standalone implementation
    private final List<BakedModelEntry> overrides = new ArrayList<>();

    public void override(Object key, BakedModel bakedModel) {
        overrides.add(new BakedModelEntry(key, bakedModel));
    }

    public BakedModel apply(...) {
        // Custom logic
    }
}
*//*?}*/
```

### TypeElytra.java - Item Detection

**Problem**: `ElytraItem` class removed

**Solution**: Direct item comparison

```java
/*? <1.21.4 {*/
if (item instanceof ElytraItem) {
    // Check using instanceof
}
/*?} else {*/
/*if (item == Items.ELYTRA) {
    // Check using registry
}
*//*?}*/
```

### ArmorFeatureRendererMixin.java - Texture Interception

**Problem**: `ArmorMaterial.Layer` removed

**Solution**: New mixin targeting `EquipmentAsset`

```java
/*? <1.21.4 {*/
@WrapOperation(method = "renderArmor",
    at = @At(value = "INVOKE",
    target = "...ArmorMaterial$Layer;getTexture..."))
public Identifier interceptOld(ArmorMaterial.Layer layer, ...) {
    // Old interception
}
/*?} else {*/
/*@WrapOperation(method = "renderArmor",
    at = @At(value = "INVOKE",
    target = "...EquipmentAsset;getTexture..."))
public Identifier interceptNew(Object equipmentAsset, ...) {
    // New interception
}
*//*?}*/
```

## Adding New Code

When adding new features that may be affected by version differences:

### Step 1: Check API Availability

Research whether the API exists in both versions:
- Minecraft Wiki changelogs
- Yarn mappings comparison
- Community documentation

### Step 2: Add Conditional Imports

```java
/*? <1.21.4 {*/
import net.minecraft.class.MaybeRemoved;
/*?}*/
```

### Step 3: Implement Version-Specific Logic

```java
public void myNewFeature() {
    /*? <1.21.4 {*/
    // Implementation for old versions
    /*?} else {*/
    /*// Implementation for new versions
    *//*?}*/
}
```

### Step 4: Test Both Paths (if possible)

- Set active version in `stonecutter.gradle`
- Verify compilation for both versions
- Check generated code

### Step 5: Document Limitations

Add comments explaining:
- Why the conditional is needed
- What changed between versions
- Any limitations of the implementation

Example:
```java
/*? <1.21.4 {*/
// Use ModelLoader for pre-1.21.4 versions
/*?} else {*/
/*// ModelLoader was renamed to BakedModelManager in 1.21.4
// The loadModelFromJson method was also removed, so this
// entire mixin is disabled for 1.21.4 pending reimplementation
*//*?}*/
```

## Build Configuration

### Changing Target Version

Edit `stonecutter.gradle`:

```gradle
stonecutter.active "1.21.4" /* [SC] DO NOT EDIT */
```

Or use Stonecutter commands:
```bash
./gradlew stonecutter:setActive -Pversion=1.21.4
```

### Build Requirements by Version

**Minecraft < 1.21**:
- Java 17+
- Gradle 8.9+
- Fabric Loom 1.7+

**Minecraft >= 1.21**:
- Java 21+
- Gradle 8.12+
- Fabric Loom 1.9.2+

The build automatically selects the correct Java version based on target Minecraft version.

## Troubleshooting

### Stonecutter Comments in Output

**Problem**: Preprocessor directives appear in compiled code

**Cause**: Directive placed inside string literals or annotations

**Fix**: Move directive outside string literals

```java
// ❌ Wrong
@Inject(method = "/*? <1.21.4 {*/oldMethod/*?} else {*//*newMethod*//*?}*/")

// ✅ Correct
/*? <1.21.4 {*/
@Inject(method = "oldMethod")
/*?} else {*/
/*@Inject(method = "newMethod")*/
/*?}*/
```

### Compilation Errors

**Problem**: Code doesn't compile for one version

**Check**:
1. All imports are conditionally handled
2. Method signatures match the target version
3. Mixin targets exist in target version
4. Field types are compatible

### Runtime Errors

**Problem**: Code compiles but crashes at runtime

**Check**:
1. Mixin target methods exist and have correct signatures
2. Reflection code handles version differences
3. Null checks for version-specific features
4. Accessor mixins target correct fields

## Current Limitations

### For 1.21.4 Builds

1. **Model Loading**: ModelLoader-based registration disabled
   - Item CIT models may not load correctly
   - Workaround: Pending BakedModelManager implementation

2. **Model Overrides**: Simplified implementation
   - Basic override storage works
   - Complex condition matching not implemented
   - Workaround: Single override per item

3. **Armor Textures**: Estimated API usage
   - May need adjustment with actual 1.21.4 builds
   - Method signature based on documentation
   - Workaround: Test and adjust when available

## Testing Checklist

Before committing changes that add conditionals:

- [ ] Code compiles for pre-1.21.4 (if possible)
- [ ] Code compiles for 1.21.4 (if possible)
- [ ] Conditional blocks are balanced (every `/*?` has `/*?}*/`)
- [ ] Comments explain why conditional is needed
- [ ] Both paths are functionally equivalent (or limitations documented)
- [ ] No Stonecutter directives inside string literals
- [ ] README.md updated if new limitation added

## Resources

- [Stonecutter Documentation](https://stonecutter.kikugie.dev/)
- [Fabric Wiki](https://fabricmc.net/wiki/)
- [Yarn Mappings](https://github.com/FabricMC/yarn)
- Main README.md - Section "Minecraft 1.21.4 Support"
- CHANGELOG_1.21.4.md - Detailed change history

## Questions?

When in doubt:
1. Check existing conditional patterns in the codebase
2. Look at similar changes in CHANGELOG_1.21.4.md
3. Follow the patterns in this guide
4. Add detailed comments explaining your approach
5. Document any new limitations in README.md

Remember: It's better to disable a feature with a TODO than to implement a potentially broken workaround.
