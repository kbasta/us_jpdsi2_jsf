package com.jsfcourse.calc;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean
public class CalcBB {
	
	private static final String PAGE_SHOWRESULT = "/pages/calc/showresult";
	private static final String PAGE_STAY_AT_THE_SAME = null;
	
	private String x;
	private String y;
	private Double result;
	
	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public String calc() {
		FacesContext ctx = FacesContext.getCurrentInstance();		
		
		try {
			double x = Double.parseDouble(this.x);
			double y = Double.parseDouble(this.y);
			
			result = x + y;
			
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operacja wykonana poprawnie", null));
			return PAGE_SHOWRESULT; 
		} catch (Exception e) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas przetwarzania parametrów", null));
			return PAGE_STAY_AT_THE_SAME; 
		}
				
	}

}
