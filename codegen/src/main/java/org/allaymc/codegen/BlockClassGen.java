package org.allaymc.codegen;

import com.palantir.javapoet.*;
import lombok.SneakyThrows;
import org.allaymc.dependence.BlockId;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.allaymc.codegen.BlockIdEnumGen.MAPPED_BLOCK_PALETTE_NBT;
import static org.allaymc.codegen.BlockPropertyTypeGen.BLOCK_PROPERTY_TYPE_INFO_FILE;

/**
 * @author daoge_cmd | Cool_Loong
 */
public class BlockClassGen extends BaseClassGen {

    public static final MethodSpec.Builder BLOCK_TYPE_DEFAULT_INITIALIZER_METHOD_BUILDER =
            MethodSpec.methodBuilder("init")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    public static Map<Pattern, String> MERGED_BLOCKS = new LinkedHashMap<>();

    public static void main(String[] args) {
        BlockIdEnumGen.generate();
        BlockPropertyTypeGen.generate();
        generate();
    }

    @SneakyThrows
    public static void generate() {
        registerMergedBlocks();

        var interfaceDir = Path.of("api/src/main/java/org/allaymc/api/block/interfaces");
        if (!Files.exists(interfaceDir)) {
            Files.createDirectories(interfaceDir);
        }
        var implDir = Path.of("server/src/main/java/org/allaymc/server/block/impl");
        if (!Files.exists(implDir)) {
            Files.createDirectories(implDir);
        }

        var typesClass = TypeSpec
                .classBuilder(ClassNames.BLOCK_TYPES)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(ClassNames.MINECRAFT_VERSION_SENSITIVE)
                .addJavadoc("Automatically generated by {@code org.allaymc.codegen.BlockClassGen}");

        for (var id : BlockId.values()) {
            typesClass.addField(
                    FieldSpec.builder(ParameterizedTypeName.get(ClassNames.BLOCK_TYPE, generateClassFullName(id)), id.name())
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .build()
            );

            var interfaceSimpleName = generateClassSimpleName(id);
            var interfaceFullName = generateClassFullName(id);
            var path = interfaceDir.resolve(interfaceSimpleName + ".java");
            if (!Files.exists(path)) {
                System.out.println("Generating " + interfaceSimpleName + "...");
                if (!Files.exists(interfaceDir)) {
                    Files.createDirectories(interfaceDir);
                }
                generateInterface(ClassNames.BLOCK_BEHAVIOR, interfaceFullName, path);
            }

            var implSimpleName = generateClassSimpleName(id) + "Impl";
            var implFullName = ClassName.get("org.allaymc.server.block.impl", implSimpleName);
            var implPath = implDir.resolve(implSimpleName + ".java");
            if (!Files.exists(implPath)) {
                System.out.println("Generating " + implSimpleName + "...");
                if (!Files.exists(implDir)) {
                    Files.createDirectories(implDir);
                }
                generateBlockImpl(interfaceFullName, implFullName, implPath);
            }

            addDefaultBlockTypeInitializer(id, implFullName);
        }
        generateDefaultBlockTypeInitializer();

        var javaFile = JavaFile.builder(ClassNames.BLOCK_TYPES.packageName(), typesClass.build())
                .indent(CodeGenConstants.INDENT)
                .skipJavaLangImports(true)
                .build();
        System.out.println("Generating " + ClassNames.BLOCK_TYPES.simpleName() + ".java ...");
        Files.writeString(Path.of("api/src/main/java/org/allaymc/api/block/type/" + ClassNames.BLOCK_TYPES.simpleName() + ".java"), javaFile.toString());
    }

    protected static void generateBlockImpl(ClassName superInterfaceName, ClassName className, Path path) throws IOException {
        TypeSpec.Builder codeBuilder = TypeSpec.classBuilder(className)
                .superclass(ClassNames.BLOCK_BEHAVIOR_IMPL)
                .addSuperinterface(superInterfaceName)
                .addModifiers(Modifier.PUBLIC);
        codeBuilder.addMethod(
                MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(ClassNames.LIST, ParameterizedTypeName.get(ClassNames.COMPONENT_PROVIDER, WildcardTypeName.subtypeOf(ClassNames.COMPONENT))), "componentProviders")
                        .addStatement("super(componentProviders)")
                        .build()
        );
        var javaFile = JavaFile.builder(className.packageName(), codeBuilder.build())
                .indent(CodeGenConstants.INDENT)
                .skipJavaLangImports(true)
                .build();
        System.out.println("Generating " + className + ".java ...");
        Files.writeString(path, javaFile.toString());
    }

    private static void addDefaultBlockTypeInitializer(BlockId id, ClassName blockClassName) {
        var initializer = CodeBlock.builder();
        initializer
                .add("$T.$N = $T\n", ClassNames.BLOCK_TYPES, id.name(), ClassNames.ALLAY_BLOCK_TYPE)
                .add("        .builder($T.class)\n", blockClassName)
                .add("        .vanillaBlock($T.$N)\n", ClassNames.BLOCK_ID, id.name());
        var blockPaletteData = MAPPED_BLOCK_PALETTE_NBT.get(id.getIdentifier().toString());
        var states = blockPaletteData.getCompound("states");
        if (!states.isEmpty()) {
            initializer.add("        .setProperties(");
            AtomicInteger count = new AtomicInteger();
            states.forEach((name, value) -> {
                var propertyName = BLOCK_PROPERTY_TYPE_INFO_FILE.differentSizePropertyTypes.contains(name.replaceAll(":", "_")) && BLOCK_PROPERTY_TYPE_INFO_FILE.specialBlockTypes.containsKey(id.getIdentifier().toString()) ?
                        BLOCK_PROPERTY_TYPE_INFO_FILE.specialBlockTypes.get(id.getIdentifier().toString()).get(name.replaceAll(":", "_")).toUpperCase() : name.replaceAll(":", "_").toUpperCase();
                initializer.add("$T.$N" + (states.size() == count.incrementAndGet() ? "" : ", "), ClassNames.BLOCK_PROPERTY_TYPES, propertyName);
            });
            initializer.add(")\n");
        }
        initializer.add("        .build()");
        BLOCK_TYPE_DEFAULT_INITIALIZER_METHOD_BUILDER
                .beginControlFlow("if ($T.$N == null)", ClassNames.BLOCK_TYPES, id.name())
                .addStatement(initializer.build())
                .endControlFlow();
    }

    @SneakyThrows
    private static void generateDefaultBlockTypeInitializer() {
        var filePath = Path.of("server/src/main/java/org/allaymc/server/block/type/BlockTypeDefaultInitializer.java");
        Files.deleteIfExists(filePath);
        var folderPath = filePath.getParent();
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        TypeSpec.Builder builder =
                TypeSpec.classBuilder(ClassNames.BLOCK_TYPE_DEFAULT_INITIALIZER)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addJavadoc("Automatically generated by {@code org.allaymc.codegen.BlockClassGen}");
        builder.addMethod(BLOCK_TYPE_DEFAULT_INITIALIZER_METHOD_BUILDER.build());

        var javaFile = JavaFile.builder(ClassNames.BLOCK_TYPE_DEFAULT_INITIALIZER.packageName(), builder.build())
                .indent(CodeGenConstants.INDENT)
                .skipJavaLangImports(true)
                .build();
        System.out.println("Generating " + ClassNames.BLOCK_TYPE_DEFAULT_INITIALIZER.simpleName() + ".java ...");
        Files.writeString(filePath, javaFile.toString());
    }

    private static ClassName generateClassFullName(BlockId id) {
        var simpleName = generateClassSimpleName(id);
        return ClassName.get("org.allaymc.api.block.interfaces", simpleName);
    }

    private static String generateClassSimpleName(BlockId id) {
        var origin = "Block" + Utils.convertToPascalCase(id.getIdentifier().path()) + "Behavior";
        for (var entry : MERGED_BLOCKS.entrySet()) {
            if (entry.getKey().matcher(origin).find()) {
                return entry.getValue();
            }
        }
        return origin;
    }

    private static void registerMergedBlock(Pattern regex, String className) {
        MERGED_BLOCKS.put(regex, className);
    }

    private static void registerMergedBlocks() {
        registerMergedBlock(Pattern.compile(".*(Leaves\\d?|LeavesFlowered)Behavior"), "BlockLeavesBehavior");
        registerMergedBlock(Pattern.compile(".*AnvilBehavior"), "BlockAnvilBehavior");
        registerMergedBlock(Pattern.compile(".*ShulkerBoxBehavior"), "BlockShulkerBoxBehavior");
        registerMergedBlock(Pattern.compile(".*StairsBehavior"), "BlockStairsBehavior");
        registerMergedBlock(Pattern.compile(".*ColoredTorch.*Behavior"), "BlockColoredTorchBehavior");
        registerMergedBlock(Pattern.compile(".*RedstoneTorchBehavior"), "BlockRedstoneTorchBehavior");
        registerMergedBlock(Pattern.compile(".*BlastFurnaceBehavior"), "BlockBlastFurnaceBehavior");
        registerMergedBlock(Pattern.compile(".*FurnaceBehavior"), "BlockFurnaceBehavior");
        registerMergedBlock(Pattern.compile(".*SmokerBehavior"), "BlockSmokerBehavior");
        registerMergedBlock(Pattern.compile(".*WoodBehavior"), "BlockWoodBehavior");
        registerMergedBlock(Pattern.compile(".*LogBehavior"), "BlockWoodBehavior");
        registerMergedBlock(Pattern.compile(".*BambooBlockBehavior"), "BlockWoodBehavior");
        registerMergedBlock(Pattern.compile(".*CrimsonHyphaeBehavior"), "BlockWoodBehavior");
        registerMergedBlock(Pattern.compile(".*CrimsonStemBehavior"), "BlockWoodBehavior");
        registerMergedBlock(Pattern.compile(".*WarpedHyphaeBehavior"), "BlockWoodBehavior");
        registerMergedBlock(Pattern.compile(".*WarpedStemBehavior"), "BlockWoodBehavior");
        registerMergedBlock(Pattern.compile(".*IronDoorBehavior"), "BlockIronDoorBehavior");
        registerMergedBlock(Pattern.compile(".*CopperDoorBehavior"), "BlockCopperDoorBehavior");
        registerMergedBlock(Pattern.compile(".*DoorBehavior"), "BlockDoorBehavior");
        registerMergedBlock(Pattern.compile(".*HangingSignBehavior"), "BlockHangingSignBehavior");
        registerMergedBlock(Pattern.compile(".*StandingSignBehavior"), "BlockSignBehavior");
        registerMergedBlock(Pattern.compile(".*WallSignBehavior"), "BlockSignBehavior");
        registerMergedBlock(Pattern.compile(".*ButtonBehavior"), "BlockButtonBehavior");
        registerMergedBlock(Pattern.compile(".*WoolBehavior"), "BlockWoolBehavior");
        registerMergedBlock(Pattern.compile("BlockElement.*"), "BlockElementBehavior");
        registerMergedBlock(Pattern.compile(".*WallBehavior"), "BlockWallBehavior");
        registerMergedBlock(Pattern.compile(".*TerracottaBehavior"), "BlockTerracottaBehavior");
        registerMergedBlock(Pattern.compile(".*CopperBehavior"), "BlockCopperBehavior");
        registerMergedBlock(Pattern.compile(".*(?:Water|Lava)Behavior"), "BlockLiquidBehavior");
        registerMergedBlock(Pattern.compile(".*PlanksBehavior"), "BlockPlanksBehavior");
        registerMergedBlock(Pattern.compile(".*GlassBehavior"), "BlockGlassBehavior");
        registerMergedBlock(Pattern.compile(".*StainedGlassBehavior"), "BlockGlassBehavior");
        registerMergedBlock(Pattern.compile(".*GlassPaneBehavior"), "BlockGlassPaneBehavior");
        registerMergedBlock(Pattern.compile(".*StainedGlassPaneBehavior"), "BlockGlassPaneBehavior");
        registerMergedBlock(Pattern.compile(".*CandleBehavior"), "BlockCandleBehavior");
        registerMergedBlock(Pattern.compile(".*CandleCakeBehavior"), "BlockCandleCakeBehavior");
        registerMergedBlock(Pattern.compile(".*LightBlock.*Behavior"), "BlockLightBlockBehavior");
        registerMergedBlock(Pattern.compile(".*CarpetBehavior"), "BlockCarpetBehavior");
        registerMergedBlock(Pattern.compile(".*Slab\\d?Behavior"), "BlockSlabBehavior");
        registerMergedBlock(Pattern.compile(".*SaplingBehavior"), "BlockSaplingBehavior");
        registerMergedBlock(Pattern.compile(".*CoralFan.*"), "BlockCoralFanBehavior");
        registerMergedBlock(Pattern.compile(".*CoralWallFanBehavior"), "BlockCoralWallFanBehavior");
        registerMergedBlock(Pattern.compile(".*CoralBehavior"), "BlockCoralBehavior");
        registerMergedBlock(Pattern.compile(".*CoralBlockBehavior"), "BlockCoralBlockBehavior");
        registerMergedBlock(Pattern.compile(".*ConcreteBehavior"), "BlockConcreteBehavior");
        registerMergedBlock(Pattern.compile(".*ConcretePowderBehavior"), "BlockConcretePowderBehavior");
        registerMergedBlock(Pattern.compile(".*FenceBehavior"), "BlockFenceBehavior");
        registerMergedBlock(Pattern.compile(".*FenceGateBehavior"), "BlockFenceGateBehavior");
        registerMergedBlock(Pattern.compile(".*(Head|Skull)Behavior"), "BlockHeadBehavior");
        registerMergedBlock(Pattern.compile(".*BricksBehavior"), "BlockBricksBehavior");
        registerMergedBlock(Pattern.compile(".*IronTrapdoorBehavior"), "BlockIronTrapdoorBehavior");
        registerMergedBlock(Pattern.compile(".*CopperTrapdoorBehavior"), "BlockCopperTrapdoorBehavior");
        registerMergedBlock(Pattern.compile(".*TrapdoorBehavior"), "BlockTrapdoorBehavior");
        registerMergedBlock(Pattern.compile(".*SandstoneBehavior"), "BlockSandstoneBehavior");
        registerMergedBlock(Pattern.compile(".*FireBehavior"), "BlockFireBehavior");
    }
}
