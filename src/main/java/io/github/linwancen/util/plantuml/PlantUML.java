package io.github.linwancen.util.plantuml;

public class PlantUML {
    public static String start() {
        return "@startuml\n" +
                "hide empty circle\n" +
                "hide empty members\n" +
                "left to right direction\n" +
                "skinparam shadowing false\n" +
                "skinparam componentStyle rectangle\n" +
                "skinparam defaultTextAlignment center\n";
    }

    public static String end() {
        return "\n@enduml";
    }
}
