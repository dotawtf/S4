package S4;
/**
 * Created by yyawesome on 15/11/20.
 */
public class Sample {
    private double[] d;
    private double[] sx;
    private double[] sx2;
    private int label;
    private int[] idp;
    public Sample(String s)
    {
        String[] str=s.split(",");
        int N= str.length;
        this.d=new double[N-1];
        this.sx = new double[N];
        this.sx2 = new double[N];
        this.label = new Integer(str[0]);
        sx[0]=0;
        sx2[0]=0;
        for(int i=1;i<N;i++) {
            d[i-1] = new Double(str[i]);
            sx[i]=sx[i-1]+d[i-1];
            sx2[i]=sx2[i-1]+d[i-1]*d[i-1];
        }
        this.setIdp(new PLR_LFDP(d).getLFDPIndexByNumber((int)(d.length*0.05+2)));
    }
    public double[] getSubSequence(int index, int length){
    	double[]  subseq = new double[length];
    	for(int i=0; i<length; i++)
    		subseq[i] = this.d[index+i];
    	return subseq;
    }
    public double[] normalize(int startindex, int length) {
		// TODO Auto-generated method stub
    	double[] result = new double[length];
    	double mean = (this.getSx()[startindex + length] - this.getSx()[startindex])/length;
    	double sdev = Math.sqrt((this.getSx2()[startindex + length]-this.getSx2()[startindex])/length-mean*mean);
    	if(sdev>0.0001){
    		for(int i=0; i<length; i++)
        		result[i] = (this.d[startindex + i] - mean)/sdev;
    	}
    	else{
    		for(int i=0; i<length; i++)
        		result[i] = 0;
    	}
		return result;
	}
    public double distanceToShapelet(double[] shapelet) {
		// TODO Auto-generated method stub
        double minDs = Double.POSITIVE_INFINITY;
        for(int i=0;i<this.d.length-shapelet.length+1;i++)
        {
            double sum=0;
            double mean = (this.sx[i+shapelet.length]-this.sx[i])/shapelet.length;
            double sdev = Math.sqrt((this.sx2[i+shapelet.length]-this.sx2[i])/shapelet.length-mean*mean);
            if(sdev>0.0001) {
                for (int j = 0; j < shapelet.length; j++) {
                    double dsj = shapelet[j] - (this.d[i + j] - mean) / sdev;
                    sum += dsj * dsj;
                    if (sum > minDs)
                        break;
                }
                if (sum < minDs)
                    minDs = sum;
            }
            else
            {
                for (int j = 0; j < shapelet.length; j++) {
                    double dsj = shapelet[j] - 0.0;//标准差小于0.0001视为数据无波动，标准化后为0，因为平均值等于每一点数值
                    sum += dsj*dsj;
                    if (sum > minDs)
                        break;
                }
                if (sum < minDs)
                    minDs = sum;
            }
        }
        return minDs;
	}
    
    public double[] getD() {
        return d;
    }

    public void setD(double[] d) {
        this.d = d;
    }

    public double[] getSx() {
        return sx;
    }

    public void setSx(double[] sx) {
        this.sx = sx;
    }

    public double[] getSx2() {
        return sx2;
    }

    public void setSx2(double[] sx2) {
        this.sx2 = sx2;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }


	public int[] getIdp() {
		return idp;
	}


	public void setIdp(int[] idp) {
		this.idp = idp;
	}
}
