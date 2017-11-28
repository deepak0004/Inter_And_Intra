import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.jimple.toolkits.annotation.purity.DirectedCallGraph;
import soot.jimple.toolkits.annotation.purity.SootMethodFilter;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.UnitGraph;

public class InterproceduralWrapper extends SceneTransformer implements SootMethodFilter
{
	@Override
	protected void internalTransform(String phaseName, Map options) 
	{
		// TODO Auto-generated method stub
		CallGraphBuilder cg = new CallGraphBuilder();
		cg.build();
		CallGraph g = cg.getCallGraph();
				
		DirectedGraph dc = new DirectedCallGraph(g,new InterproceduralWrapper(),g.sourceMethods(),true);
		PseudoTopologicalOrderer pto = new PseudoTopologicalOrderer();
	    List order = pto.newList(dc, true);
	    HashMap< String, HashMap< Integer, Integer > > context = new HashMap< String, HashMap< Integer, Integer > >();
	
	    for(int i=0; i<(int)order.size(); i++)
		{
			SootMethod method = (SootMethod) order.get(i);
			G.v().out.println("Analysing " + method.getName());
			
			String methodname = (String)method.getName();
			//System.out.println(methodname);
			if( methodname.equals("<init>") )
			{
				//System.out.println("yo2");
				continue;
			}
			
			UnitGraph graph = new BriefUnitGraph(method.getActiveBody());
			
			if( !methodname.equals("main") )
			{
				//System.out.println("yo");
				InterproceduralAnalysis reach = new InterproceduralAnalysis(graph, context, 1, 0);
				context = reach.getMap();
				reach = new InterproceduralAnalysis(graph, context, 2, 0);
				context = reach.getMap();
			    reach = new InterproceduralAnalysis(graph, context, 3, 0);
				context = reach.getMap();
			    reach = new InterproceduralAnalysis(graph, context, 4, 0);				
				context = reach.getMap();
				
				reach = new InterproceduralAnalysis(graph, context, 5, 0);
				context = reach.getMap();
				reach = new InterproceduralAnalysis(graph, context, 6, 0);
				context = reach.getMap();
			    reach = new InterproceduralAnalysis(graph, context, 7, 0);
				context = reach.getMap();
			    reach = new InterproceduralAnalysis(graph, context, 8, 0);				
				context = reach.getMap();
				
				reach = new InterproceduralAnalysis(graph, context, 9, 0);
				context = reach.getMap();
				reach = new InterproceduralAnalysis(graph, context, 10, 0);
				context = reach.getMap();
			    reach = new InterproceduralAnalysis(graph, context, 11, 0);
				context = reach.getMap();
			    reach = new InterproceduralAnalysis(graph, context, 12, 0);				
				context = reach.getMap();
				
				reach = new InterproceduralAnalysis(graph, context, 13, 0);
				context = reach.getMap();
				reach = new InterproceduralAnalysis(graph, context, 14, 0);
				context = reach.getMap();
			    reach = new InterproceduralAnalysis(graph, context, 15, 0);
				context = reach.getMap();
			}
			else 
			{
				InterproceduralAnalysis reach = new InterproceduralAnalysis(graph, context, 4, 1);				
				context = reach.getMap();
			}
		}
	    //System.out.println( context.get("bar").get(0) );
	}
	
	@Override
	public boolean want(SootMethod m) 
	{
		List<SootMethod> methods = Scene.v().getMainMethod().getDeclaringClass().getMethods();
		if(methods.contains(m))
			return true;
		else
			return false;
		// TODO Auto-generated method stub
	}
}