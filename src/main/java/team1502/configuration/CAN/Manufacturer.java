package team1502.configuration.CAN;

public enum Manufacturer {
    NI                  ("NI", 1),
    LuminaryMicro       ("Luminary Micro", 2),
    DEKA                ("DEKA", 3),
    CTRElectronics      ("CTR Electronics", 4),
    REVRobotics         ("REV Robotics", 5),
    Grapple             ("Grapple", 6),
    MindSensors         ("MindSensors", 7),
    TeamUse             ("Team Use", 8),
    KauaiLabs           ("Kauai Labs", 9),
    Copperforge         ("Copperforge", 10),
    PlayingWithFusion   ("Playing With Fusion", 11),
    Studica             ("Studica", 12),
    TheThriftyBot       ("The Thrifty Bot", 13),
    ReduxRobotics       ("Redux Robotics", 14);

    public String ManufacturerName;
    public int ManufacturerID;
    private Manufacturer(String name, int id) { ManufacturerName = name; ManufacturerID = id;}
}
