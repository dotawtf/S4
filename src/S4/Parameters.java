package S4;

public class Parameters {

    public static String userpath =  "time_series_data/";

    public static String[] fileNames={
            //Number of train, test cases, length, classes
    		"Adiac",                    // 390,391,176,37
            "Beef",						// 30,30,470,5
            "ChlorineConcentration",    // 467,3840,166,3 
            "Coffee",                   // 28,28,286,2
            "DiatomSizeReduction",      // 16,306,345,4
            "ECGFiveDays",              // 23,861,136,2
            "FaceFour",                 // 24,88,350,4
            "Gun_Point",                 // 50,150,150,2
            "ItalyPowerDemand",         // 67,1029,24,2
            "Lighting7",                // 70,73,319,7
            "MoteStrain",               // 20,1252,84,2
            "SonyAIBORobotSurface",     // 20,601,70,2
            "Symbols",                  // 25,995,398,6
            "Synthetic_control",         // 300,300,60,6
            "Trace",                    // 100,100,275,4
            "TwoLeadECG",               // 23,1139,82,2
    };

    public static double[][] para = { 
    	//MaxLength, MinLength, alpha, C
        {104,26,3,1}, //Adiac * 104,26 0.665
        {120,30,1,10},  //Beef  * 120,30,1 86.7
        {40,10,0.5,1},   //ChlorineConcentration * 0.5,76.8 1,75.8 1.5,75.6
        {48,12,1,10},    //Coffee * 
        {140,70,0.5,10},   //DiatomSizeReduction 140,70,0.5 95.1
        {80,20,0,10},    //ECGFiveDays * 80,20,0
        {80,10,1,10},  //FaceFour *     
    	{40,10,2,10},    //GunPoint *  80,20,2 99.3
        {16,8,0.5,10}, //ItalyPowerDemand *16,8,0.5 95
        {120,30,1.5,10}, //Lighting7 * 
        {50,6,0,10},  //MoteStrain * 
        {48,6,0.5,10},   //SonyAIBORobotSurface * 48,6,0.5 94
        {90,45,1,10},  //Symbols * 
        {30,30,0,10},  //SyntheticControl * 30,30,0 0.97
        {40,20,3,10},     //Trace *
        {24,6,0.5,10},  //TwoLeadECG * 24,6,0.5 97.7
    };
}
