package Reporter;

import reporting.Reporter;

public class ReporterTest {

	public static void main(String[] args) {
		
		Reporter reporter = new Reporter();
		try {
			reporter.createReport("a-standard", "rolling", 100000, 2);
		
			System.out.println("ID="+reporter.id);
			reporter.updateReport(1, 500, "");
			reporter.updateReport(2, 600, "");
			reporter.updateReport(3, 700, "");
			reporter.updateReport(4, 400, "");
			reporter.updateReport(5, 300, "human");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
