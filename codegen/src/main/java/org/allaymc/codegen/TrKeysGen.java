package org.allaymc.codegen;

import com.google.gson.JsonParser;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.allaymc.dependence.Identifier;

import javax.lang.model.element.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author daoge_cmd | IWareQ
 */
public class TrKeysGen {
    static final Path TR_EN_FILE_PATH = Path.of(CodeGenConstants.DATA_PATH + "lang/en_US.json");
    static final Path OUTPUT_PATH = Path.of("api/src/main/java/org/allaymc/api/i18n/TrKeys.java");

    @SneakyThrows
    public static void main(String[] args) {
        var keys = JsonParser
                .parseReader(Files.newBufferedReader(TR_EN_FILE_PATH))
                .getAsJsonObject()
                .keySet();
        var codeBuilder = TypeSpec.interfaceBuilder(ClassNames.TR_KEYS)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Automatically generated by {@code org.allaymc.codegen.TrKeysGen}");
        for (var key : keys) {
            var identifier = new Identifier(key);
            var namespace = handleNamespace(identifier.namespace());
            var path = identifier.path().replaceAll("\\.", "_").replaceAll("-", "_").toUpperCase();
            var fieldName = namespace + "_" + path;
            codeBuilder.addField(
                    FieldSpec
                            .builder(ClassNames.STRING, fieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\"" + key + "\"")
                            .build()
            );
        }
        var javaFile = JavaFile.builder(ClassNames.TR_KEYS.packageName(), codeBuilder.build())
                .indent(CodeGenConstants.INDENT)
                .skipJavaLangImports(true)
                .build();
        System.out.println("Generating " + ClassNames.TR_KEYS.simpleName() + ".java ...");
        Files.deleteIfExists(OUTPUT_PATH);
        Files.createFile(OUTPUT_PATH);
        Files.writeString(OUTPUT_PATH, javaFile.toString());
    }

    protected static String handleNamespace(String origin) {
        return switch (origin) {
            case "minecraft" -> "M";
            case "allay" -> "A";
            default -> origin.toUpperCase();
        };
    }
}
