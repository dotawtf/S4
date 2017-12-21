package S4;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dist.Dist;
import dist.DistCompare;

public class SubclassSample {
	private Map<Integer, String> classMap;	
	public SubclassSample() {
		super();
		// TODO Auto-generated constructor stub
		this.classMap=new HashMap<Integer, String>();
	}
	
	public Map<Integer, String> getClassMap() {
		return classMap;
	}

	public void setClassMap(Map<Integer, String> classMap) {
		this.classMap = classMap;
	}

	private double[] sumValue(Data data) {
		double[] sumValue = new double[data.getData().size()];
		for (int i = 0; i < data.getData().size(); i++) {
			sumValue[i] = 0;
			Sample sample = data.getData().get(i);
			for (int j = 0; j < sample.getD().length; j++) {
				sumValue[i] += sample.getD()[j];
			}
		}
		return sumValue;
	}
	
	private Map<Integer, List<Integer>> classStatistics(Data data) {
		Map<Integer, List<Integer>> classStatistics = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < data.getData().size(); i++) {
			Sample sample = data.getData().get(i);
			int classValue = sample.getLabel();
			if (!classStatistics.containsKey(classValue)) {
				List<Integer> list = new ArrayList<Integer>();
				list.add(i);
				classStatistics.put(classValue, list);
			} else {
				List<Integer> list = classStatistics.get(classValue);
				list.add(i);
				classStatistics.put(classValue, list);
			}
		}
		return classStatistics;
	}
	
	private Map<Integer, Integer> classPivot(Map<Integer, List<Integer>> classStatistics, double[] sumValue) {
		Map<Integer, Integer> classPivot = new HashMap<Integer, Integer>();
		Iterator entries = classStatistics.entrySet().iterator();
		List<Integer> list = new ArrayList<Integer>();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			list = (ArrayList<Integer>) entry.getValue();
			double classSum = 0;
			for (int i = 0; i < list.size(); i++) {
				int index = list.get(i);
				classSum += sumValue[index];
			}
			double classMeanValue = classSum / list.size();
			int pivotIndex=list.get(0);
			double min=Math.abs(sumValue[pivotIndex]-classMeanValue);		
			for (int i = 1; i < list.size(); i++) {
				int index=list.get(i);
				double diff=Math.abs(sumValue[index]-classMeanValue);
				if(diff<min){
					min=diff;
					pivotIndex=index;
				}
			}
			classPivot.put((Integer) entry.getKey(), pivotIndex);
		}		
		return classPivot;
	}
	
	private void subClassSplittingForOneClass(Data data, int classValue,int privotIndex,List<Integer> SampleIndexList){
		List<Dist> distList=new ArrayList<Dist>();
		Sample pivot=data.getData().get(privotIndex);
		for(int i=0;i<SampleIndexList.size();i++){
			int index=SampleIndexList.get(i);
			Sample sample=data.getData().get(index);
			double dist=0;
			for(int j=0;j<sample.getD().length;j++){
				dist+=Math.pow(pivot.getD()[j]-sample.getD()[j], 2);
			}
			dist=Math.sqrt(dist);
			distList.add(new Dist(index,dist));
		}
		Collections.sort(distList, new DistCompare());
		
		double[] diff=new double[distList.size()-1];
		for(int i=0;i<distList.size()-1;i++){
			diff[i]=distList.get(i+1).getDist()-distList.get(i).getDist();
		}
		//double T=std(diff)/2;
		double T=std(diff);
		int C=1;
				
		classMap.put(distList.get(0).getSampleIndex(), classValue+"_"+C);
		classMap.put(distList.get(1).getSampleIndex(), classValue+"_"+C);
		for(int i=2;i<=diff.length;i++){
			if(diff[i-1]>T){
				C++;
			}
			classMap.put(distList.get(i).getSampleIndex(), classValue+"_"+C);
		}
	} 
	
	public List<Integer> subClassSplitting(Data data){
		Map<Integer, List<Integer>> map = classStatistics(data);
		double[] s = sumValue(data);
		Map<Integer, Integer> classPivot = classPivot(map, s);
		
		classMap=new HashMap<Integer, String>();
		Iterator entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next();
			Integer classValue=(Integer)entry.getKey();
			List<Integer> sampleIndexList=(List<Integer>)entry.getValue();
			Integer privotIndex=classPivot.get(classValue);
			
			if(sampleIndexList.size()>=2){
				subClassSplittingForOneClass(data,classValue,privotIndex,sampleIndexList);
			
			}else{
				classMap.put(sampleIndexList.get(0),classValue+"_1");
			}
		}
		List<Integer> sampleIndex=getIndex(data);
		//System.out.println(sampleIndex.size());
		return sampleIndex;
		
	}

	private List<Integer> getIndex(Data data){
		double[] dist=new double[data.getData().size()];
		for(int i=0;i<data.getData().size();i++){
			dist[i]=0;
		}
		for(int i=0;i<data.getData().size()-1;i++){
			for(int j=i+1;j<data.getData().size();j++){
				if(classMap.get(i).equals(classMap.get(j))){
					Sample iSample=data.getData().get(i);
					Sample jSample=data.getData().get(j);
					double distance=0;
					for(int k=0;k<iSample.getD().length;k++)
						distance+=Math.pow(iSample.getD()[k]-jSample.getD()[k], 2);
					distance=Math.sqrt(distance);		
					dist[i]+=distance;
					dist[j]+=distance;
				}
			}
		}
		Map<String,Integer> sampleIndex=new HashMap<String,Integer>();
		for(int i=0;i<data.getData().size();i++){
			if(sampleIndex.containsKey(classMap.get(i))){
				int j=sampleIndex.get(classMap.get(i));
				if(dist[i]<dist[j]){
					sampleIndex.put(classMap.get(i),i);
				}
				
			}else{
				sampleIndex.put(classMap.get(i),i);
			}
		}
		
		Iterator entries = sampleIndex.entrySet().iterator();  
		List<Integer> list=new ArrayList<Integer>(); 
		while (entries.hasNext()) {  		  
		    Map.Entry entry = (Map.Entry) entries.next();  
		    Integer value = (Integer)entry.getValue();  
		    list.add(value); 	  
		} 
		return list;
	}
	
	private double mean(double[] data){
		int k=data.length;
		double sum=0;
		for(int i=0;i<k;i++){
			sum+=data[i];
		}
		return sum/k;
	}
	
	private double std(double[] data){
		double mean=mean(data);
		int k=data.length;
		double std=0;
		for(int i=0;i<k;i++){
			std+=Math.pow(data[i]-mean, 2);
		}
		//此处需要二次确定
		std=std/k;
		//std=std/(k-1);
		std=Math.sqrt(std);
		return std;
	}
	
	public static void main(String[] args) throws IOException {
	    String filePath = "time_series_data" + File.separator + "Adiac" + File.separator + "Adiac" + "_TRAIN";
		Data train = new Data(filePath);
		List<Integer> sampleIndex=new SubclassSample().subClassSplitting(train);
		System.out.println(sampleIndex.size());
	}
	
}
