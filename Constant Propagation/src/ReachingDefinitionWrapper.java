import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.SootMethod;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

public class ReachingDefinitionWrapper extends BodyTransformer
{
	@Override
	protected void internalTransform(Body b, String phase, Map options) 
	{
		// TODO Auto-generated method stub
		SootMethod sootMethod = b.getMethod();	
		UnitGraph g = new BriefUnitGraph(sootMethod.getActiveBody());  // graph is created
		Chain<Local> lc = b.getLocals();
		Iterator<Local> it = lc.iterator();
		int i = 0;
		ArrayList<String> reaching_def = new ArrayList<String>();
		if(sootMethod.isStatic())
		{
			while(it.hasNext())
			{
				String var = it.next().getName();
     			reaching_def.add(var);				
			}
		}
		else
		{
			while(it.hasNext())
			{
			    String var = it.next().getName();
				if(!var.equals("this"))
				{
					reaching_def.add(var);
		     	}
			}
			
		}
		ReachingDefinitionAnalysis reach = new ReachingDefinitionAnalysis(g, reaching_def);
	}
}