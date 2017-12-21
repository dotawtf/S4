package S4;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public class Run {
    public static void main(String[] args) throws IOException{
        for(int i=0;i<Parameters.fileNames.length;i++)
        {
            System.out.println(Parameters.fileNames[i]+":");
            Data traindata = new Data(Parameters.userpath+Parameters.fileNames[i]+"/"+Parameters.fileNames[i]+"_TRAIN");  
            Data testdata = new Data(Parameters.userpath+Parameters.fileNames[i]+"/"+Parameters.fileNames[i]+"_TEST");
            Long start=new Date().getTime();        
            S4forShapeletDiscovery shapeletsLearner=new S4forShapeletDiscovery((int)Parameters.para[i][0],(int)Parameters.para[i][1],Parameters.para[i][2],Parameters.para[i][3]);
            shapeletsLearner.train(traindata);
            Long end = new Date().getTime();
            System.out.println("    Train time:"+(double)(end-start)/1000+"s");
            System.out.println("    accuracy:" + 100 * shapeletsLearner.computeAccuracy(testdata) + "%");
        }
    }
}
