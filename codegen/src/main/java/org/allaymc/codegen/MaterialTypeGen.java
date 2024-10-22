package org.allaymc.codegen;

import com.palantir.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.allaymc.codegen.ClassNames.MAP;
import static org.allaymc.codegen.ClassNames.STRING;

/**
 * @author daoge_cmd | IWareQ
 */
public class MaterialTypeGen {
    static final Path MATERIAL_DATA_FILE_PATH = Path.of(CodeGenConstants.DATA_PATH + "materials.json");

    public static void main(String[] args) throws IOException {
        generate();
    }

    public static void generate() throws IOException {
        var interfaceBuilder = TypeSpec.interfaceBuilder("MaterialTypes")
                .addJavadoc("Automatically generated by {@code org.allaymc.codegen.MaterialTypeGen}")
                .addModifiers(Modifier.PUBLIC)
                .addField(
                        FieldSpec.builder(ParameterizedTypeName.get(MAP, STRING, ClassNames.MATERIAL_TYPE), "NAME_TO_MATERIAL_TYPE")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer("new $T<>()", HashMap.class)
                                .build()
                );

        var keys = Utils.parseKeys(MATERIAL_DATA_FILE_PATH);
        for (var key : keys) {
            interfaceBuilder.addField(
                    FieldSpec
                            .builder(ClassNames.MATERIAL_TYPE, Utils.camelCaseToSnakeCase(key).toUpperCase())
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("create($S)", key)
                            .build()
            );
        }

        interfaceBuilder.addMethod(
                MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ClassNames.MATERIAL_TYPE)
                        .addParameter(String.class, "name")
                        .addStatement("var tag = new $T(name)", ClassNames.MATERIAL_TYPE)
                        .addStatement("NAME_TO_MATERIAL_TYPE.put(name, tag)")
                        .addStatement("return tag")
                        .build()
        ).addMethod(
                MethodSpec.methodBuilder("getMaterialTypeByName")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ClassNames.MATERIAL_TYPE)
                        .addParameter(String.class, "name")
                        .addStatement("return NAME_TO_MATERIAL_TYPE.get(name)")
                        .build()
        );

        var javaFile = JavaFile.builder("org.allaymc.api.block.material", interfaceBuilder.build())
                .indent(CodeGenConstants.INDENT)
                .skipJavaLangImports(true)
                .build();
        Files.writeString(Path.of("api/src/main/java/org/allaymc/api/block/material/MaterialTypes.java"), javaFile.toString());
    }
}
