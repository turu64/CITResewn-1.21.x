<p align="center">
  <img src="https://citresewn.shcm.io/img/project_description/logo_shadow.png" width="200px">
</p>

CIT Resewn is MCPatcher's CIT features re-written outside of optifine as a standalone mod for fabric.

The main CIT Resewn mod serves as an API to add types and conditions while CIT Resewn: Defaults uses that API to provide the default types and conditions that naturally come with the CIT format.

## Downloads
You can get CIT Resewn(bundled with Defaults) from Modrinth, Curse Forge or by compiling it from source

              <a href="https://modrinth.com/mod/cit-resewn"><img src="https://citresewn.shcm.io/img/modrinth.png" width="50px"></a>       
<a href="https://www.curseforge.com/minecraft/mc-mods/cit-resewn"><img src="https://citresewn.shcm.io/img/curseforge.png" width="50px"></a>

## CIT Docs
Docs for CIT Resewn's usage are available over at https://citresewn.shcm.io

## API

CIT Resewn is distributed for development through Modrinth's Maven repository under `cit-resewn`.<br>
Defaults can be added separately through the same Maven under `cit-resewn-defaults`.

Gradle example:
```groovy
// Add the modrinth maven repository:
repositories {
    ..
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    ..
    // Add the base CIT Resewn API to the project
    modCompileOnly "maven.modrinth:cit-resewn:1.1.2+1.19.2"
    // Add Defaults to the project
    modCompileOnly "maven.modrinth:cit-resewn-defaults:1.1.2+1.19.2"
}
```

API usage documentation will be available soon over at [the docs](https://citresewn.shcm.io/mods/mod_api/).

For example usage of the CIT Resewn API, take a look at how Defaults does it.

## Minecraft 1.21.4 Support

This branch includes experimental support for Minecraft 1.21.4, which required significant API adaptations due to breaking changes in Minecraft's codebase.

### Implementation Status

The 1.21.4 support was implemented in three phases:

#### Phase 1: Conditional Imports (Commit: 88a093d)
Added conditional preprocessing using Stonecutter to handle removed/changed classes:
- `ModelLoader` → `BakedModelManager` (class renamed)
- `ModelOverride` / `ModelOverrideList` (removed)
- `ElytraItem` (removed)
- `ArmorMaterial.Layer` (removed, replaced with equipment assets)

All code using these classes is wrapped in version conditionals (`/*? <1.21.4 {*/ ... /*?}*/`) to compile for both old and new versions.

#### Phase 2: Module Re-enablement (Commit: 79bdcf9)
Re-enabled the `defaults` module for Minecraft 1.21.4 after Phase 1 conditional processing.

#### Phase 3: Alternative API Implementation (Commit: 748f0b6)
Implemented 1.21.4-specific alternatives for removed APIs:

**TypeItem.java**:
- Custom `CITOverrideList` implementation that doesn't extend the removed `ModelOverrideList`
- Simplified model override storage and application logic
- Conditional processing in `getItemModel()` for different override systems

**TypeElytra.java**:
- Elytra detection using `Items.ELYTRA` comparison instead of `instanceof ElytraItem`
- Maintains warning functionality for non-elytra items

**ArmorFeatureRendererMixin.java**:
- Armor texture replacement using new `EquipmentAsset` API
- Wrapped texture interception for new equipment system

**ItemRendererMixin.java**:
- Conditional import handling for `ItemModels` (potential API changes)

### Known Limitations

1. **CITOverrideList Implementation**: The current implementation is simplified. Full model override matching logic based on item properties needs further refinement.

2. **Armor Texture Replacement**: The `EquipmentAsset` API method signature is estimated and may require adjustment when compiled against actual 1.21.4 builds.

3. **ModelLoader-based Mixins**: These remain disabled for 1.21.4 (wrapped in conditionals). Full migration of the model loading system is pending.

4. **Testing**: All 1.21.4 implementations require testing against actual Minecraft 1.21.4 builds for validation and bug fixing.

### Build Requirements

For Minecraft 1.21.4:
- Java 21 or higher
- Gradle 8.12+
- Fabric Loom 1.9.2
- Fabric API 0.113.0+1.21.4

The build system automatically selects the correct Java version based on the target Minecraft version (Java 17 for <1.21, Java 21 for ≥1.21).

### Stonecutter Preprocessing

This project uses [Stonecutter](https://stonecutter.kikugie.dev/) for multi-version support. The preprocessor directives follow this format:

```java
/*? <1.21.4 {*/
// Code for Minecraft versions below 1.21.4
import net.minecraft.client.render.model.ModelLoader;
/*?} else {*/
/*// Code for Minecraft 1.21.4+
import net.minecraft.client.render.model.BakedModelManager;
*//*?}*/
```

The active version is set in `stonecutter.gradle`.

## Contributing

Bug fixes and feature implementations are always welcome and will usually be accepted once verified to be ok/fit in the mod.

Translations aren't really necessary but if you PR them there's no reason they won't be accepted.

Lastly, the docs site is also open source, PRs can be made to the [docs branch](https://github.com/SHsuperCM/CITResewn/tree/docs).
