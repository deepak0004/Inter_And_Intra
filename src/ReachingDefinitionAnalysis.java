import java.util.*;  

import soot.Body;
import soot.Value;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.JastAddJ.Expr;
import soot.JastAddJ.ModExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.DivExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.MulExpr;
import soot.jimple.OrExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;

public class ReachingDefinitionAnalysis extends ForwardFlowAnalysis 
{
	Body b;
	ArrayList<String> reaching_d;
	public ReachingDefinitionAnalysis(UnitGraph g, ArrayList<String> reaching_def)
	{
		super(g);
		b = g.getBody();
		reaching_d = reaching_def;
		doAnalysis();
	}
	@Override
	protected void flowThrough(Object in, Object unit, Object out) 
	{
		FlowSet inval, outval;

		Map<String, Integer> mapp3 = new HashMap<String, Integer>();
		Map<String, Integer> mapp2, mapp; // = new HashMap<String, Integer>();  
		
		inval = (FlowSet)in;   // entry set
		outval = (FlowSet)out; // output set
		Stmt u = (Stmt)unit; // statment
		
		//inval.copy(outval); // inval to outval
		// x = a*b
		// x-> defbox
		// a, b, a*b -> usebox
		
		Iterator inIt = inval.iterator();
		while (inIt.hasNext()) 
		{
			mapp3 = (Map<String, Integer>) inIt.next();
		}
	    mapp = new HashMap<String, Integer>(mapp3);
	    mapp2 = new HashMap<String, Integer>(mapp3);

		// Kill operation
		Iterator<ValueBox> defIt = u.getDefBoxes().iterator();
		while(defIt.hasNext())
		{
			ValueBox defBox = (ValueBox)defIt.next();
			
			if (defBox.getValue() instanceof Local) // variables in RHS are local
			{
				//System.out.println("killed " + defBox.getValue());
				if( mapp.get(defBox.getValue().toString()) != null )
				{
					//System.out.println("killed2 " + defBox.getValue());
					mapp.remove(defBox.getValue().toString());
				}
			}
		}
	
		//Gen operation
		if( u instanceof AssignStmt )
		{ 
			Iterator<ValueBox> usIt = u.getUseBoxes().iterator();
			int a = Integer.MIN_VALUE, b = Integer.MIN_VALUE, c = Integer.MIN_VALUE;
			boolean flag1 = false, flag2 = false;
			while(usIt.hasNext())
			{
				ValueBox uBox = (ValueBox)usIt.next();
				if( flag1==false )
				{
					 if( uBox.getValue() instanceof Local )
					 {
						  if( mapp2.containsKey(uBox.getValue().toString()) )
						  {
							  a = (int) mapp2.get(uBox.getValue().toString());
						  }
					 }
					 else if( uBox.getValue() instanceof IntConstant )
					 {
						 String cons = uBox.getValue().toString();
					     a = Integer.parseInt(cons);
					 }
					 c = a;
					 flag1 = true;
				}
				else if( flag2==false )
				{
					if( uBox.getValue() instanceof Local )
					{
						  if( mapp2.containsKey(uBox.getValue().toString()) )
						  {
							  b = (int) mapp2.get(uBox.getValue().toString());
						  }
					}
				    else if( uBox.getValue() instanceof IntConstant )
					{
						 String cons = uBox.getValue().toString();
						 b = Integer.parseInt(cons);
					}
					c = b;
					flag2 = true;
				}
				else 
				{
					if( a==Integer.MIN_VALUE || b==Integer.MIN_VALUE )
					{
						c = Integer.MIN_VALUE;
						break;
					}
					if( uBox.getValue() instanceof AddExpr ) 
				    {
				    	c = a+b;
				    }
					else if( uBox.getValue() instanceof SubExpr ) 
				    {
				    	c = a-b;
				    }
					else if( uBox.getValue() instanceof MulExpr ) 
				    {
				    	c = a*b;
				    }
					else if( uBox.getValue() instanceof DivExpr ) 
				    {
				    	c = a/b;
				    }
					else 
					{
						c = a%b;
					}
			    }
			}
			if( c != Integer.MIN_VALUE )
			{             
				//System.out.println("c: " + c);
				mapp.put((String)u.getDefBoxes().get(0).getValue().toString(), c);
			}                            
		}                              
		outval.add(mapp);  	 
		                                                 
		if ( u instanceof AssignStmt || u instanceof ReturnStmt || u instanceof IfStmt )
		{
			System.out.println("In: ");
			for (Map.Entry<String, Integer> entry : mapp2.entrySet())
			{
			    System.out.println( "(" + entry.getKey() + ", " + entry.getValue() + ")");
			}
			System.out.println("Unit: " + u);
			System.out.println("Out :");
			for (Map.Entry<String, Integer> entry : mapp.entrySet())
			{
			    System.out.println( "(" + entry.getKey() + ", " + entry.getValue() + ")");
			}	
			System.out.println();
		}
	}
	
	@Override
	protected void copy(Object source, Object dest) 
	{
		FlowSet srcSet = (FlowSet)source;
		FlowSet	destSet = (FlowSet)dest;
		srcSet.copy(destSet);
	}
	
	// Inset for binit (empty here)
	@Override
	protected Object entryInitialFlow() 
	{		
		//return new ArraySparseSet();
		FlowSet in = new ArraySparseSet();
		/*Map<String, Integer> mapp = new HashMap<String, Integer>();
		for (int i=0; i<(int)reaching_d.size(); i++) 
		{
			//System.out.println("vars " + reaching_d.get(i));
			mapp.put(reaching_d.get(i), Integer.MAX_VALUE);
		}
		in.add(mapp);*/
		
		return in;
	}
	
	@Override
	protected void merge(Object in1, Object in2, Object out) 
    {
		Map<String, Integer> mapp = new HashMap<String, Integer>();
		Map<String, Integer> mapp2 = new HashMap<String, Integer>();
		Map<String, Integer> mapp3 = new HashMap<String, Integer>();
		Map<String, Integer> mapp4 = new HashMap<String, Integer>();
		Map<String, Integer> mapp5 = new HashMap<String, Integer>();
		
		FlowSet inval1 = (FlowSet)in1; 
		FlowSet inval2 = (FlowSet)in2; 
		FlowSet outSet = (FlowSet)out; 
	   
		Iterator inIt = inval1.iterator();
		while (inIt.hasNext()) 
		{
			mapp4 = (Map<String, Integer>) inIt.next();
		}
		Iterator inIt2 = inval2.iterator();
		while (inIt2.hasNext()) 
		{
			mapp5 = (Map<String, Integer>) inIt2.next();
		}
		mapp = new HashMap<String, Integer>(mapp4);
		mapp2 = new HashMap<String, Integer>(mapp5);
		/*System.out.println(inval1.size());
		System.out.println(inval2.size());*/
		/*System.out.println("merge");
		for (Map.Entry<String, Integer> entry : mapp.entrySet())
		{
			System.out.println( "(" + entry.getKey() + ", " + entry.getValue() + ")");
		}
		System.out.println("yo");
		for (Map.Entry<String, Integer> entry : mapp2.entrySet())
		{
			System.out.println( "(" + entry.getKey() + ", " + entry.getValue() + ")");
		}
		System.out.println("/merge");*/
		for (Map.Entry<String, Integer> entry : mapp.entrySet())
		{
			if( mapp2.get( entry.getKey() )!=null && ( mapp2.get( entry.getKey() )==entry.getValue() || mapp2.get( entry.getKey() )==Integer.MAX_VALUE ) )
	     	    mapp3.put( entry.getKey(), entry.getValue() );
			/*else if( entry.getValue()==Integer.MAX_VALUE && mapp2.get( entry.getKey() )!=null )
				mapp3.put( entry.getKey(), mapp2.get( entry.getKey() ) );   
			*/
		}
		
		outSet.add(mapp3);
		/*FlowSet inval1=(FlowSet)in1;
		FlowSet inval2=(FlowSet)in2;
		FlowSet outSet=(FlowSet)out;
		// Must analysis
		inval1.intersection(inval2, outSet);*/
	}

	// Inset for every statement except initial (empty here)
	@Override
	protected Object newInitialFlow() 
	{
		Map<String, Integer> mapp = new HashMap<String, Integer>();
		FlowSet in = new ArraySparseSet();
		for (int i=0; i<(int)reaching_d.size(); i++) 
		{
			//System.out.println("vars " + reaching_d.get(i));
			mapp.put(reaching_d.get(i), Integer.MAX_VALUE);
		}
		in.add(mapp);
		
		return in;
	}
}