
public class QueueTest {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		java.util.concurrent.LinkedBlockingDeque<Integer> q = new java.util.concurrent.LinkedBlockingDeque<Integer>();
		new Thread(new InsertThread(q)).start();
		new Thread(new WorkerThread(q)).start();
		
		
	 
		//ExponentialDistribution d = new ExponentialDistribution(10);
//		PoissonDistribution p = new PoissonDistribution(0.5);
//		
//		while (true)
//		{
//		    long interval = Math.round(p.sample());
//		 System.out.println(interval);
//		    // Fire event here.
//		}

	}
	
	static class InsertThread implements Runnable {
		
		java.util.concurrent.LinkedBlockingDeque<Integer> q;
		
		public InsertThread(java.util.concurrent.LinkedBlockingDeque<Integer> q) {
			this.q = q;
		}

		@Override
		public void run() {
			int i = 0;
			while (i < 100){
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				q.add(i++);
			}
		}
		
	}
	
	static class WorkerThread implements Runnable {
		
		java.util.concurrent.LinkedBlockingDeque<Integer> q;
		
		public WorkerThread(java.util.concurrent.LinkedBlockingDeque<Integer> q) {
			this.q = q;
		}

		@Override
		public void run() {
			while (true) {
				if (q.isEmpty()){
					System.out.println("EMPTY");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				} else
				System.out.println(q.poll() +" !");
			}
		}
		
	}

}
