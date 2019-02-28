plugins{
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

tasks.jar {
    enabled = false
}
