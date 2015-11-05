package junit.plp.core.modules;

import plptool.Constants;
import plptool.PLPSimBusModule;

public class MockModule extends PLPSimBusModule
{
	public MockModule(long startAddr, long endAddr, boolean wordAligned)
	{
		super(startAddr, endAddr, wordAligned);
	}
	
	public MockModule()
	{
		super();
	}

	@Override
	public void run()
	{
		super.run();
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int eval()
	{
		start();
		
		return Constants.PLP_OK;
	}
	
	@Override
	public int gui_eval(Object x)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void reset()
	{
		interrupt();
	}
	
	@Override
	public String introduce()
	{
		return "The Best Mock Module in the world";
	}
	
}