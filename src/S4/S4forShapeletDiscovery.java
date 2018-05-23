package S4;
import de.bwaldvogel.liblinear.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by yyawesome on 15/11/20.
 */

public class S4forShapeletDiscovery {
    private int maxLength;
    private int minLength;
    private double alpha;
    private double C = 1;    // cost of constraints violation
    private Model model;
    private HashMap<double[],Integer> shapelets;
    public S4forShapeletDiscovery(int maxLength, int minLength, double alpha,double C) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.alpha = alpha;
        this.C= C;  
    }

    public void train(Data data)
    {
        this.shapelets = new HashMap<>();
        ArrayList<double[]> candidataShapelets = new ArrayList<>();
        HashSet<Integer> item_labels = data.getLabels();
        List<Integer> sampleIndex=new SubclassSample().subClassSplitting(data);
        for(int label:item_labels){
        	ArrayList<Sample> class1 = new ArrayList<>();
    		ArrayList<Sample> rest = new ArrayList<>();
    		for(int index:sampleIndex){
    			Sample s = data.getData().get(index);
    			if(s.getLabel() == label) 
    				class1.add(s);
    			else 
    				rest.add(s); 			
    		}

    		for(int Len=maxLength;Len>=minLength;Len/=2)
            {
    			for(Sample sA:class1){
    					for(Sample sB:rest){
    					ArrayList<Integer> idp_index = new ArrayList<>();
    					for(int i=0;i<sA.getD().length-Len+1;i++){
    						for(int j=0;j<sA.getIdp().length;j++){
    							if(sA.getIdp()[j]>=i&&sA.getIdp()[j]<=i+Len-1)
    							{
    								idp_index.add(i);
    								break;
    							}
    						}
    					}
    					if(idp_index.size()==0) continue;
    					double[] PAB = calculateISSV_multi(sA, sB, Len);
    	        		double[] PAA = calculateISSV_self(sA, Len);
    	        		double[] diffAB = new double[idp_index.size()];
    	        		for(int i=0; i<diffAB.length; i++) diffAB[i] = Double.NaN;
    	        		double sum = 0;
    	        		int number =0;
    	        		for(int i=0; i<idp_index.size(); i++){
    	        			int index = idp_index.get(i);
    	        			if(!Double.isNaN(PAB[index])&&!Double.isNaN(PAA[index])){
    	        				diffAB[i] = PAB[index] - PAA[index];
    	        				sum += diffAB[i];
        	        			number++;
    	        			}
    	        		}
    	        		ArrayList<Integer> wave = new ArrayList<>();
    	        		int direction = -1;
    	        		for(int i=0; i<idp_index.size()-1; i++){
    	        			if((diffAB[i+1]-diffAB[i])*direction>0){
    	        				direction*=-1;
    	        				if(direction==1) wave.add(i);
    	        			}
    	        		}
    	        		double mean = sum/number;
    	        		double sigma = 0;
    	        		for(int i=0; i<idp_index.size(); i++){
    	        			if(Double.isNaN(diffAB[i])) continue;
    	        			sigma+= (diffAB[i]-mean)*(diffAB[i]-mean);
    	        		}
    	        		sigma = Math.sqrt(sigma/number);
//    	        		double threshold = mean+alpha*sigma; original
    	        		double threshold = mean+alpha*sigma;
    	        		if(sigma == 0) threshold = mean;
    	        		for(int i=0; i<idp_index.size(); i++){
    	        			if(Double.isNaN(diffAB[i])) continue;
//    	        			if(diffAB[i] >= threshold){
    	        			if(diffAB[i] >= threshold && wave.contains(i)){
    	        					candidataShapelets.add(sA.normalize(idp_index.get(i), Len));
    	        			}
    	        		}
    	        	}
    			}
            }
    		class1.clear();
    		rest.clear();
    	}
        Feature[][] features = new Feature[data.getData().size()][candidataShapelets.size()];
        double[] labels=new double[data.getData().size()];
        for(int i=0;i<data.getData().size();i++)
        {            
            for(int j=0;j<candidataShapelets.size();j++) {
            	  double d = data.getData().get(i).distanceToShapelet(candidataShapelets.get(j));
            	  features[i][j] = new FeatureNode(j + 1,d);
            }
            labels[i] = (double)data.getData().get(i).getLabel();
        }
        Problem problem = new Problem();
        problem.l = data.getData().size(); // number of training examples
        problem.n = candidataShapelets.size(); // number of features  
        problem.x = features; // feature nodes   
        problem.y = labels; // target values
        SolverType solver = SolverType.getById(5); // -s 5

        double eps = 0.0001; // stopping criteria
        Parameter parameter = new Parameter(solver, C, eps, 10000);
        Linear.disableDebugOutput();
        Linear.resetRandom();
        System.out.println("    Start to train L1-regularized L2-loss support vector classification!");
        this.model = Linear.train(problem, parameter);

        HashSet<Integer> zerofeatures =new HashSet<Integer>();
        for(int i=1;i<=candidataShapelets.size();i++)
        {
            boolean zero = true;
            for(int j=0;j<data.getLabels().size();j++)
            {
                if(Math.abs(model.getDecfunCoef(i,j))>0.0001)
                    zero = false;
            }
            if(zero)
                zerofeatures.add(i);
        }

        for(int i=1;i<=candidataShapelets.size();i++)
        {
            if(!zerofeatures.contains(i))
                shapelets.put(candidataShapelets.get(i-1),i);
        }
    }

    public double[] calculateISSV_multi(Sample TA, Sample TB, int Len) {
		// TODO Auto-generated method stub
    	double[] PAB = new double[TA.getD().length - Len + 1];
    	double[] B1 = TB.getSubSequence(0, Len);
    	double[] A1 = TA.getSubSequence(0, Len);
    	double miuB1 = (TB.getSx()[Len] - TB.getSx()[0])/Len;
    	double miuA1 = (TA.getSx()[Len] - TA.getSx()[0])/Len;
    	double sigmaB1 = Math.sqrt((TB.getSx2()[Len] - TB.getSx2()[0])/Len - miuB1*miuB1);
    	double sigmaA1 = Math.sqrt((TA.getSx2()[Len] - TA.getSx2()[0])/Len - miuA1*miuA1);
    	ArrayList<double[]> result1 = MASS(B1, TA, miuB1, sigmaB1);
    	ArrayList<double[]> result2 = MASS(A1, TB, miuA1, sigmaA1);
    	double[] D = result1.get(0);
    	double[] QT = result1.get(1);
    	double[] DB = result2.get(0);
    	double[] QTB = result2.get(1);
    	for(int i=0; i<PAB.length; i++) PAB[i] = D[i];
    	double miuQ, sigmaQ;
    	double[] MT = new double[TA.getD().length - Len + 1];
    	double[] SigmaT = new double[TA.getD().length - Len + 1];
    	//计算均值和标准差向量
    	for(int i=0; i<MT.length; i++){
    		MT[i] = (TA.getSx()[i+Len] - TA.getSx()[i])/Len;
    		SigmaT[i] = Math.sqrt((TA.getSx2()[i + Len]- TA.getSx2()[i])/Len-MT[i]*MT[i]);
    	}
    	for(int i=1; i<TB.getD().length-Len+1; i++){
    		for(int j=TB.getD().length-Len; j>0; j--)
    			QT[j] = QT[j-1] - TA.getD()[j-1]*TB.getD()[i-1] + TA.getD()[j+Len-1]*TB.getD()[i+Len-1];
    		QT[0] = QTB[i];
    		miuQ = (TB.getSx()[i+Len] - TB.getSx()[i])/Len;
    		sigmaQ = Math.sqrt((TB.getSx2()[i+Len] - TB.getSx2()[i])/Len - miuQ*miuQ);
    		for(int j=0; j<QT.length; j++)
        		D[j] = Math.sqrt(Math.abs(2*Len*(1 - (QT[j]-Len*miuQ*MT[j]) / (Len*sigmaQ*SigmaT[j])))); 
    		for(int j=0; j<D.length; j++) PAB[j] = Math.min(D[j], PAB[j]);
    	}
		return PAB;
	}
    public double[] calculateISSV_self(Sample T, int Len) {
		// TODO Auto-generated method stub
    	double[] PAA = new double[T.getD().length - Len + 1];
    	int exclusion_zone = Len/4;
    	double[] T1 = T.getSubSequence(0, Len);  	
    	double miuT1 = (T.getSx()[Len] - T.getSx()[0])/Len;
    	double sigmaT1 = Math.sqrt((T.getSx2()[Len] - T.getSx2()[0])/Len - miuT1*miuT1);
    	ArrayList<double[]> result = MASS(T1, T, miuT1, sigmaT1);
    	double[] D = result.get(0);
    	for(int i=0; i<D.length; i++) {
    		if(i<exclusion_zone) PAA[i] = Double.POSITIVE_INFINITY;//将trivial match设为inf
    		else PAA[i] = D[i];
    	}
    	double[] QT = result.get(1); 
    	double[] QT_c = new double[QT.length];
    	for(int i=0; i<QT.length; i++) QT_c[i]=QT[i];
    	double[] MT = new double[T.getD().length - Len + 1];
    	double[] SigmaT = new double[T.getD().length - Len + 1];
    	//计算均值和标准差向量
    	for(int i=0; i<MT.length; i++){
    		MT[i] = (T.getSx()[i+Len] - T.getSx()[i])/Len;
    		SigmaT[i] = Math.sqrt((T.getSx2()[i + Len]- T.getSx2()[i])/Len-MT[i]*MT[i]);
    	}
    	for(int i=1; i<T.getD().length-Len+1; i++){
    		for(int j=T.getD().length-Len; j>0; j--)
    			QT[j] = QT[j-1] - T.getD()[j-1]*T.getD()[i-1] + T.getD()[j+Len-1]*T.getD()[i+Len-1];
    		QT[0] = QT_c[i];
    		for(int j=0; j<QT.length; j++)
        		D[j] = Math.sqrt(Math.abs(2*(Len - (QT[j]-Len*MT[i]*MT[j]) / (SigmaT[i]*SigmaT[j]))));
    		for(int j=0; j<D.length; j++){
    			if(Math.abs(j-i)<exclusion_zone) continue;//avoid trivial match
    			PAA[j] = Math.min(D[j], PAA[j]);
    		}
    	}
		return PAA;
	}
    public ArrayList<double[]> MASS(double[] Q, Sample T, double miuQ, double sigmaQ) {
		// TODO Auto-generated method stub
    	int subseq_num = T.getD().length - Q.length + 1;  	
    	ArrayList<double[]> result = new ArrayList<>();
    	double[] D = new double[subseq_num];
    	double[] MT = new double[subseq_num];
    	double[] SigmaT = new double[subseq_num];
    	double[] QT = SlidingDotProducts(Q, T);
    	//计算均值和标准差向量
    	for(int i=0; i<subseq_num; i++){
    		MT[i] = (T.getSx()[i+Q.length] - T.getSx()[i])/Q.length;
    		SigmaT[i] = Math.sqrt((T.getSx2()[i + Q.length]- T.getSx2()[i])/Q.length-MT[i]*MT[i]);
    	}
    	//计算距离向量
    	for(int i=0; i<subseq_num; i++){
    		D[i] = Math.sqrt(Math.abs(2*(Q.length - (QT[i]-Q.length*miuQ*MT[i])/(sigmaQ*SigmaT[i]))));
    	}
    	result.add(D);
    	result.add(QT);
		return result;
	}
    
    public double[] SlidingDotProducts(double[] Q, Sample T) {
		// TODO Auto-generated method stub
    	int subseq_num = T.getD().length - Q.length + 1;
    	double[] QT = new double[subseq_num];
    	//将T的长度乘以2，考虑后续要进行FFT，乘以2后再补到最近的2的幂
    	Complex[] Ta = new Complex[(int)Math.pow(2, Math.ceil(Math.log(T.getD().length*2)/Math.log(2)))];
    	Complex[] Qra = new Complex[Ta.length];
    	for(int i=0; i<Qra.length; i++){
    		if(i<Q.length) Qra[i] = new Complex(Q[Q.length - 1 - i], 0);
    		else Qra[i] = new Complex(0, 0);
    	}
    	for(int i=0; i<Ta.length; i++){
    		if(i<T.getD().length) Ta[i] = new Complex(T.getD()[i], 0);
    		else Ta[i] = new Complex(0, 0);
    	}
    	Complex[] QT_c = new FFT().cconvolve(Ta, Qra);
    	for(int i=0; i<QT.length; i++)
    		QT[i] = QT_c[Q.length - 1 + i].re();
		return QT;
	}

	public double computeAccuracy(Data data)
    {
        int correctSize= 0;
        for(Sample s:data.getData())
        {
            if(s.getLabel() == (int)predict(s))
                correctSize++;
        }
        System.out.println("    "+correctSize + "/" + data.getData().size());
        return  (double)correctSize/data.getData().size();
    }

    public double predict(Sample s)
    {
        return Linear.predict(model,this.transformToFeature(s));
    }
    public Feature[] transformToFeature(Sample s)
    {
        Feature[] f=new Feature[shapelets.size()];
        int index=0;
        for(double[] shapelet:shapelets.keySet())
        	f[index++] = new FeatureNode(shapelets.get(shapelet),s.distanceToShapelet(shapelet)); 
        return f;
    }
}
