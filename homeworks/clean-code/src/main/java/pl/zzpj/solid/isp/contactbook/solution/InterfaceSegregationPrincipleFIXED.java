package pl.zzpj.solid.isp.contactbook.solution;

class InterfaceSegregationPrincipleFIXED {
	
	Emailer emailer;
	Dialler dialler;
	
	public InterfaceSegregationPrincipleFIXED() {
		emailer = new Emailer();
		dialler = new Dialler();
	}
	
	public static void main(String[] args) {
		
		InterfaceSegregationPrincipleFIXED interfaceSegregationPrinciple = new InterfaceSegregationPrincipleFIXED();
		interfaceSegregationPrinciple.contactPeople();
		
	}
	
	public void contactPeople() {
		
		Contact contact = new Contact("Jan Kowalski", "Kielce", "jan.kowalski@gmail.com", "83744-23434");
		emailer.sendMessage(contact, "promocja", "tanio dzisiaj!");
		dialler.makeCall(contact);
	}
	

}
