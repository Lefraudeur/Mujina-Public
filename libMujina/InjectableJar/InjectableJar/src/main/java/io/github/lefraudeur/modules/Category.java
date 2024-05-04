package io.github.lefraudeur.modules;

import java.util.ArrayList;
import java.util.List;

public enum Category
{
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    VISUAL("Visual"),
    EXPLOIT("Exploit"),
    PLAYER("Player"),
    MISC("Misc"),
    CRYSTAL("Crystal");


    private final String name;
    private final List<Module> modules = new ArrayList<>();
    Category(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addModule(Module module)
    {
        modules.add(module);
    }

    public List<Module> getModules()
    {
        return modules;
    }
}