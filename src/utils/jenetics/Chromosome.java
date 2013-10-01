/**
 * 
 */
package utils.jenetics;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Itay
 *
 */
public class Chromosome implements Comparable<Chromosome> {
	
	private String content;
	private Double grade;
	/**
	 * @param content
	 * @param grade
	 */
	public Chromosome(String content, Double grade) {
		super();
		this.content = new String(content);
		this.grade = grade;
	}
	
	public Chromosome(Chromosome c) {
		super();
		this.content = new String(c.content);
		this.grade = c.grade;
	}
	
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the grade
	 */
	public Double getGrade() {
		return grade;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(Double grade) {
		this.grade = grade;
	}
	
	public void mutate(Double mutationRatio, Random r) {
		StringBuffer sb = new StringBuffer();
		
		for(char basicData : this.content.toCharArray()) {
			if(basicData != '\0') {
				char postMutation = '\0';
				if(mutationRatio.compareTo(r.nextDouble()) >= 0) {
					postMutation = (basicData == '1') ? '0' : '1';
				} else {
					postMutation = basicData;
				}
				sb.append(postMutation);
			}
		}
	}
	
	public static ArrayList<Chromosome> crossover(Chromosome p1, Chromosome p2, Double coRatio, Random r) {
		ArrayList<Chromosome> childs = new ArrayList<Chromosome>();
		String c1Content = null;
		String c2Content = null;
		
		if(coRatio.compareTo(r.nextDouble()) >= 0) {
			int ind1 = r.nextInt(p1.content.length());
			int ind2 = ind1 + r.nextInt(p1.content.length() - ind1);
			c1Content = firstChildCrossover(p1, p2, ind1, ind2);
			c2Content = firstChildCrossover(p2, p1, ind1, ind2);
		} else {
			c1Content = p1.content;
			c2Content = p2.content;
		}
		
		Chromosome c1 = new Chromosome(c1Content, null);
		Chromosome c2 = new Chromosome(c2Content, null);
		childs.add(c1);
		childs.add(c2);
		
		return childs;
	}
	
	private static String firstChildCrossover(Chromosome p1, Chromosome p2, int ind1, int ind2) {
		String p1Content = p1.content;
		String p2Content = p2.content;
		StringBuffer c1Content = new StringBuffer();
		
		for(int i = 0; i < ind1; ++i) {
			c1Content.append(p1Content.charAt(i));
		}
		
		for(int i = ind1; i < ind2; ++i) {
			c1Content.append(p2Content.charAt(i));
		}
		
		for(int i = ind2; i < p1Content.length(); ++i) {
			c1Content.append(p1Content.charAt(i));
		}
		
		return c1Content.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Chromosome)) {
			return false;
		}
		Chromosome other = (Chromosome) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Chromosome o) {
		// TODO Auto-generated method stub
		return this.grade.compareTo(o.grade);
	}	
	
}
