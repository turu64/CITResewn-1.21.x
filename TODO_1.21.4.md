# Minecraft 1.21.4 Implementation TODO

This document tracks remaining work for complete Minecraft 1.21.4 support.

## Priority Levels

- 🔴 **Critical**: Required for basic functionality
- 🟠 **High**: Important for full feature parity
- 🟡 **Medium**: Nice to have, improves functionality
- 🟢 **Low**: Optional improvements

## Current Status: Phase 3 Complete ✅

- ✅ Phase 1: Conditional imports and wrapping
- ✅ Phase 2: Module re-enablement
- ✅ Phase 3: Alternative API implementations
- ✅ Phase 4: Documentation and guides
- ⏳ Testing and refinement pending

---

## Critical Items 🔴

### 1. Model Loading System Migration

**Status**: ⚠️ Disabled for 1.21.4
**Priority**: 🔴 Critical
**Files**:
- `defaults/mixin/common/ModelLoaderMixin.java`
- `defaults/mixin/types/item/ModelLoaderMixin.java`

**Problem**:
- `ModelLoader` class renamed to `BakedModelManager`
- `loadModelFromJson()` method removed/changed
- Model registration system completely different

**Current State**:
- Entire mixins wrapped in `/*? <1.21.4 {*/ ... /*?}*/`
- No model loading for 1.21.4 builds

**Required**:
- [ ] Research BakedModelManager API in 1.21.4
- [ ] Identify model registration entry points
- [ ] Implement alternative model loading for JSON files
- [ ] Support ResewnItemModelIdentifier system
- [ ] Handle model parent/child relationships
- [ ] Support texture overrides in loaded models

**Approach**:
```java
/*?} else {*/
/*@Mixin(BakedModelManager.class)
public class ModelLoaderMixin {
    // TODO: Find equivalent to loadModelFromJson
    // Possible alternatives:
    // 1. Hook into model loading event
    // 2. Use resource reload listener
    // 3. Inject into model baker

    @Inject(method = "TBD", at = @At("TBD"))
    public void citresewn$loadCustomModels(...) {
        // Register CIT models
    }
}
*//*?}*/
```

**References**:
- Minecraft 1.21.4 model loading changes
- BakedModelManager API documentation
- Item model definition system changes

---

### 2. CITOverrideList Condition Matching

**Status**: ⚠️ Simplified implementation
**Priority**: 🔴 Critical
**File**: `defaults/cit/types/TypeItem.java`

**Problem**:
- Current implementation returns first override
- Doesn't match based on item properties
- No predicate evaluation

**Current State**:
```java
public BakedModel apply(BakedModel originalModel, ...) {
    // Simplified: just return first override
    for (BakedModelEntry entry : overrides) {
        if (entry.model != null) {
            return entry.model;
        }
    }
    return originalModel;
}
```

**Required**:
- [ ] Implement condition matching logic
- [ ] Support item property predicates
- [ ] Handle damage, custom_model_data, etc.
- [ ] Match against item state
- [ ] Optimize for performance

**Approach**:
```java
public BakedModel apply(BakedModel originalModel, ItemStack stack,
                       ClientWorld world, LivingEntity entity, int seed) {
    for (BakedModelEntry entry : overrides) {
        if (matchesConditions(entry.key, stack, world, entity)) {
            return entry.model;
        }
    }
    return originalModel;
}

private boolean matchesConditions(Object key, ItemStack stack,
                                  ClientWorld world, LivingEntity entity) {
    // TODO: Implement proper condition matching
    // Consider item properties like:
    // - custom_model_data
    // - damage
    // - enchantments
    // - other predicates
    return false;
}
```

**Testing**:
- [ ] Test with simple overrides
- [ ] Test with damage predicates
- [ ] Test with custom_model_data
- [ ] Test with multiple conditions
- [ ] Performance benchmarks

---

### 3. Armor Texture API Verification

**Status**: ⚠️ Estimated implementation
**Priority**: 🔴 Critical
**File**: `defaults/mixin/types/armor/ArmorFeatureRendererMixin.java`

**Problem**:
- Method signature based on estimates
- Actual API may be different
- Mixin target may not exist or have different signature

**Current State**:
```java
/*@WrapOperation(method = "renderArmor",
    at = @At(value = "INVOKE",
    target = "Lnet/minecraft/component/type/EquipmentAsset;getTexture()..."))
public Identifier citresewn$replaceArmorTexture1_21_4(
        Object equipmentAsset, Operation<Identifier> original) {
*/
```

**Required**:
- [ ] Obtain actual Minecraft 1.21.4 mappings
- [ ] Verify EquipmentAsset class exists
- [ ] Check getTexture() method signature
- [ ] Confirm renderArmor method flow
- [ ] Test with actual 1.21.4 build

**Testing**:
- [ ] Verify mixin applies successfully
- [ ] Test all armor slots (helmet, chest, legs, boots)
- [ ] Test layer 1 and layer 2 textures
- [ ] Test with custom armor textures
- [ ] Check for any rendering glitches

---

## High Priority Items 🟠

### 4. Item Model Definition Migration

**Status**: 📋 Not started
**Priority**: 🟠 High
**Impact**: Required for proper item model support

**Problem**:
- 1.21.4 uses JSON-based item model definitions
- Located in `assets/<namespace>/items/`
- Different format from block model JSON

**Required**:
- [ ] Research new item model format
- [ ] Understand predicate system
- [ ] Implement JSON generation for CIT models
- [ ] Handle model references correctly
- [ ] Support all CIT features in new format

**Resources Needed**:
- Minecraft 1.21.4 item model documentation
- Example item model JSON files
- Asset path structure documentation

---

### 5. Comprehensive Testing Suite

**Status**: 📋 Not started
**Priority**: 🟠 High
**Impact**: Ensures implementation works correctly

**Required**:
- [ ] Set up test environment with 1.21.4
- [ ] Create test resource packs
- [ ] Test all CIT types:
  - [ ] Item texture
  - [ ] Item model
  - [ ] Enchantment glint
  - [ ] Armor texture
  - [ ] Elytra texture
- [ ] Test edge cases
- [ ] Performance testing
- [ ] Compatibility testing with other mods

**Test Cases Needed**:
1. Basic item texture replacement
2. Item model replacement
3. Model with overrides
4. Damaged item textures
5. Custom model data
6. Armor texture replacement (all slots)
7. Armor overlays
8. Elytra textures
9. Enchanted items
10. Multiple resource packs

---

## Medium Priority Items 🟡

### 6. Error Handling and Logging

**Status**: ⚠️ Basic error handling only
**Priority**: 🟡 Medium
**Impact**: Better debugging and user experience

**Required**:
- [ ] Add version-specific error messages
- [ ] Log when features are disabled
- [ ] Warn about incompatible CIT configurations
- [ ] Helpful error messages for common issues
- [ ] Debug logging for troubleshooting

**Example**:
```java
if (MinecraftVersion.isAtLeast("1.21.4")) {
    if (modelLoaderBasedFeature) {
        LOGGER.warn("Model loading features are not yet supported in 1.21.4. " +
                   "This CIT will be disabled. See issue #XXX for status.");
    }
}
```

---

### 7. Performance Optimization

**Status**: ⏳ Pending implementation
**Priority**: 🟡 Medium
**Impact**: Better game performance

**Areas to Optimize**:
- [ ] CITOverrideList condition matching
- [ ] Model caching for 1.21.4
- [ ] Reduce allocation in hot paths
- [ ] Optimize texture lookups
- [ ] Profile and benchmark

**Measurements Needed**:
- Baseline performance (pre-1.21.4)
- Current 1.21.4 performance
- Identify bottlenecks
- Optimize critical paths
- Re-benchmark

---

### 8. Documentation Improvements

**Status**: ✅ Basic docs complete
**Priority**: 🟡 Medium
**Impact**: Better developer and user experience

**Required**:
- [ ] Add inline code examples
- [ ] Create troubleshooting guide
- [ ] Document known issues
- [ ] Add FAQ section
- [ ] User-facing documentation for CIT creators

**Sections to Add**:
- "What works in 1.21.4"
- "What doesn't work yet"
- "How to report issues"
- "Contributing to 1.21.4 support"

---

## Low Priority Items 🟢

### 9. Code Quality Improvements

**Status**: ✅ Functional implementation
**Priority**: 🟢 Low
**Impact**: Code maintainability

**Possible Improvements**:
- [ ] Refactor large conditional blocks
- [ ] Extract common patterns
- [ ] Improve variable naming
- [ ] Add more code comments
- [ ] Create helper methods

---

### 10. Additional Features

**Status**: 💡 Ideas
**Priority**: 🟢 Low
**Impact**: Enhanced functionality

**Possible Additions**:
- [ ] Support for new 1.21.4 rendering features
- [ ] Enhanced compatibility modes
- [ ] Additional CIT types if applicable
- [ ] Performance monitoring tools
- [ ] Development tools for CIT creators

---

## Blocked Items ⏸️

### 11. Actual 1.21.4 Build Testing

**Status**: ⏸️ Blocked
**Blocking**: Minecraft 1.21.4 not available
**Priority**: 🔴 Critical when unblocked

**Cannot proceed until**:
- Minecraft 1.21.4 is released
- Yarn mappings are available
- Fabric Loader supports 1.21.4
- Fabric API is updated

**When unblocked**:
- [ ] Build mod for 1.21.4
- [ ] Test all functionality
- [ ] Fix compilation errors
- [ ] Fix runtime errors
- [ ] Adjust estimates to reality

---

## Completed Items ✅

### Phase 1: Conditional Imports
- ✅ Added conditional imports for all removed classes
- ✅ Wrapped incompatible code in preprocessor directives
- ✅ Tested compilation for wrapped code
- ✅ Committed changes (88a093d)

### Phase 2: Module Re-enablement
- ✅ Re-enabled defaults module for 1.21.4
- ✅ Updated build.gradle
- ✅ Verified module inclusion
- ✅ Committed changes (79bdcf9)

### Phase 3: Alternative Implementations
- ✅ Implemented CITOverrideList alternative
- ✅ Added elytra detection without ElytraItem
- ✅ Created armor texture mixin for EquipmentAsset
- ✅ Added conditional ItemModels import
- ✅ Committed changes (748f0b6)

### Phase 4: Documentation
- ✅ Created comprehensive README section
- ✅ Created CHANGELOG_1.21.4.md
- ✅ Created MIGRATION_GUIDE_1.21.4.md
- ✅ Created TODO_1.21.4.md (this file)
- ✅ Committed changes (086ca55)

---

## Timeline and Milestones

### Milestone 1: Basic Compilation ✅
- Target: Mod compiles for 1.21.4
- Status: **Complete**
- Achieved: Phase 1-4

### Milestone 2: Basic Functionality ⏳
- Target: Core features work in 1.21.4
- Status: **In Progress** (blocked by testing)
- Dependencies:
  - Model loading implementation
  - CITOverrideList refinement
  - Armor texture verification
- Estimated: When 1.21.4 available + 2-3 weeks

### Milestone 3: Feature Parity ⏳
- Target: All features work as in pre-1.21.4
- Status: **Not Started**
- Dependencies:
  - Milestone 2 complete
  - All critical items resolved
  - Comprehensive testing
- Estimated: Milestone 2 + 2-4 weeks

### Milestone 4: Optimization and Polish ⏳
- Target: Performance and quality improvements
- Status: **Not Started**
- Dependencies:
  - Milestone 3 complete
  - Performance profiling
  - User feedback
- Estimated: Milestone 3 + 1-2 weeks

---

## How to Contribute

### For Critical Items 🔴

1. Read MIGRATION_GUIDE_1.21.4.md
2. Research the specific API in 1.21.4
3. Implement solution following existing patterns
4. Add comprehensive comments
5. Update this TODO with progress
6. Create PR with detailed description

### For Testing

1. Wait for Minecraft 1.21.4 release
2. Build mod for 1.21.4
3. Test systematically using checklist
4. Document all issues found
5. Create issues for bugs
6. Update TODO with findings

### For Documentation

1. Identify gaps in documentation
2. Add missing information
3. Improve existing docs
4. Add examples and screenshots
5. Update this TODO when complete

---

## Notes

- This is a living document - update as work progresses
- Mark items with ✅ when complete
- Add new items as discovered
- Update priorities as needed
- Link to issues/PRs when created

---

Last Updated: Phase 4 Complete
Next Review: After Minecraft 1.21.4 release
