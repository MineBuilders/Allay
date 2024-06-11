plugins {
    `java-library`
    id("buildlogic.common")
}

group = "org.allaymc"
description = "api"
version = "1.0.0"

dependencies {
    api(libs.network) {
        exclude(group = "org.cloudburstmc", module = "nbt") // use allay nbt
        exclude(group = "org.cloudburstmc.fastutil.commons")
        exclude(group = "org.cloudburstmc.fastutil.maps")
    }
    api(libs.blockstateupdater) {
        exclude(group = "org.cloudburstmc", module = "nbt")
    }
    api(libs.slf4j.api)
    api(libs.nbt)
    api(libs.fastutil)
    api(libs.guava)
    api(libs.gson)
    api(libs.snakeyaml)
    api(libs.annotations)
    api(libs.commonsio)
    api(libs.commonslang3)
    api(libs.joml)
    api(libs.joml.primitives)
    api(libs.okaeri.configs.yaml.snakeyaml) {
        exclude(group = "org.yaml", module = "snakeyaml")//use my latest version
    }
    api(libs.caffeine)
    implementation(libs.libdeflate)
}

tasks.clean {
    group = "alpha build"
    rootProject.rootDir.resolve(".run").listFiles { f -> !f.name.equals("Allay.run.xml") }?.forEach {
        delete(it)
    }
}