plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("io.freefair.lombok") version "8.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.badbird"
version = "5.0.0-DEV"
description = "Annotation based slash command framework for JDA"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("net.dv8tion:JDA:5.0.0-beta.24")

    compileOnly("com.google.inject:guice:7.0.0")

    implementation("org.reflections:reflections:0.10.2") {
        exclude("org.slf4j", "slf4j-api")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.build {
    dependsOn("shadowJar")
}
tasks.shadowJar {
    archiveClassifier.set("")
    relocate("org.reflections", "dev.badbird.jdacommand.relocate.reflections")
    relocate("javassist", "dev.badbird.jdacommand.relocate.reflections.javassist")
    relocate("javax", "dev.badbird.jdacommand.relocate.reflections.javax")
}
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(sourceSets["main"].allJava)
}
artifacts {
    add("archives", javadocJar)
    add("archives", sourcesJar)
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("JDACommand")
                description.set("A command framework for JDA")
                url.set("https://github.com/Badbird5907/JDACommand")
                from(components["java"])
                artifact(sourcesJar)
                artifact(javadocJar)
                scm {
                    url.set("https://github.com/Badbird5907/JDACommand.git")
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT/")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("Badbird5907")
                        name.set("Badbird5907")
                        email.set("contact@badbird.dev")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("-DEV")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("nexusUsername")
                password = System.getenv("nexusPassword")
            }
        }
    }
}
signing {
    sign(publishing.publications["mavenJava"])
}