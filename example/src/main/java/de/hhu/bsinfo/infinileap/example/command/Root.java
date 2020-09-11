package de.hhu.bsinfo.infinileap.example.command;

import picocli.CommandLine;

@CommandLine.Command(
    name = "infinileap",
    description = "",
    subcommands = {
        Devices.class, Communication.class
    }
)
public class Root implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}