package S4;
/**
 * Created by yyawesome on 15/11/20.
 */
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
    	{96,24,3,1}, //Adiac * 
        {120,30,1.6,10},  //Beef  * 
        {40,10,2,1},   //ChlorineConcentration * 
        {48,12,1.3,10},    //Coffee * 
        {160,40,0.5,10},   //DiatomSizeReduction 
        {80,20,0.4,10},    //ECGFiveDays *
        {80,10,1.5,10},  //FaceFour *     
    	{40,20,1.5,10},    //GunPoint *  
        {16,8,0.8,10}, //ItalyPowerDemand *
        {240,30,1,10}, //Lighting7 * 
        {49,12,0.8,10},  //MoteStrain * 
        {25,25,0.8,10},   //SonyAIBORobotSurface * 
        {90,90,1.5,10},  //Symbols * 
        {30,30,1,10},  //SyntheticControl * 
        {40,20,3.5,10},     //Trace *
        {24,6,0.5,10},  //TwoLeadECG * 
    };
}
