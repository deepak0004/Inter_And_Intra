import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.RValueBox;
import soot.tagkit.LineNumberTag;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class InterproceduralAnalysis extends ForwardFlowAnalysis
{
	Body b;
	FlowSet inval, outval;
	HashMap< String, HashMap< Integer, Integer > > map;
	int sign, printt;
	int[][] op1 = {
			{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15},
			
			{15, 1, 11, 1, 4, 11, 1, 7, 11, 15, 7, 11, 15, 7, 15, 15},
			{15, 11, 2, 2, 4, 11, 11, 15, 2, 9, 9, 11, 15, 15, 9, 15},
			{15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
			{15, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
			
			{15, 11, 11, 5, 4, 11, 11, 15, 11, 15, 12, 11, 15, 15, 15, 15},
			{15, 1, 11, 6, 4, 11, 6, 7, 11, 15, 13, 11, 15, 13, 15, 15},
			{15, 7, 15, 7, 4, 15, 7, 7, 15, 15, 7, 15, 15, 7, 15, 15},
			{15, 11, 2, 8, 4, 11, 11, 15, 8, 9, 14, 11, 15, 15, 14, 15},
			
			{15, 15, 9, 9, 4, 15, 15, 15, 9, 9, 9, 15, 15, 15, 9, 15},
			{15, 7, 9, 10, 4, 12, 13, 7, 14, 9, 10, 15, 12, 13, 14, 15},
			{15, 11, 11, 11, 4, 11, 11, 15, 11, 15, 15, 11, 15, 15, 15, 15},
			{15, 15, 15, 12, 4, 15, 15, 15, 15, 15, 12, 15, 15, 15, 15, 15},
			
			{15, 7, 15, 13, 4, 15, 13, 7, 15, 15, 13, 15, 15, 13, 15, 15},
			{15, 15, 9, 14, 4, 15, 15, 15, 14, 9, 14, 15, 15, 15, 14, 15},
			{15, 15, 15, 15, 4, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15}
	};
	int[] op2 = {
			15, 2, 1, 3, 4, 5, 8, 9, 6, 7, 10, 11, 12, 14, 13, 15	
	};
	int[][] megee = {
			{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15},
			
			{15, 1, 5, 6, 7, 5, 6, 7, 11, 12, 13, 11, 12, 13, 15, 15},
			{15, 5, 2, 8, 9, 5, 11, 12, 8, 9, 14, 11, 12, 15, 14, 15},
			{15, 6, 8, 3, 10, 11, 6, 13, 8, 14, 10, 11, 15, 13, 14, 15},
			{15, 7, 9, 10, 4, 12, 13, 7, 14, 9, 10, 15, 12, 13, 14, 15},

			{15, 5, 5, 11, 12, 5, 11, 7, 11, 12, 15, 11, 12, 15, 15, 15},
			{15, 6, 11, 6, 13, 11, 6, 13, 11, 15, 13, 15, 15, 13, 15, 15},
			{15, 7, 12, 13, 7, 7, 13, 7, 15, 12, 13, 15, 12, 13, 15, 15},
			{15, 11, 8, 8, 14, 11, 11, 15, 8, 14, 14, 11, 15, 15, 14, 15},
			
			{15, 12, 9, 14, 9, 12, 15, 12, 14, 9, 14, 15, 12, 15, 14, 15},
			{15, 13, 14, 10, 10, 15, 13, 13, 14, 14, 10, 15, 15, 13, 14, 15},
			{15, 11, 11, 11, 15, 11, 15, 15, 11, 15, 15, 11, 15, 15, 15, 15},
			{15, 12, 12, 15, 12, 12, 15, 12, 15, 12, 15, 15, 12, 15, 15, 15}, 
	        
			{15, 13, 15, 13, 13, 15, 13, 13, 15, 15, 13, 15, 15, 13, 15, 15},
			{15, 15, 14, 14, 14, 15, 15, 15, 14, 14, 14, 15, 15, 15, 14, 15},
			{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15}
	};

	public InterproceduralAnalysis(UnitGraph g, HashMap< String, HashMap< Integer, Integer > > map, int sign, int print)
	{
		super(g);
		b = g.getBody();
		this.map = map;
		this.sign = sign; 
		this.printt = print;
		doAnalysis();
	}
	
	@Override
	protected void flowThrough(Object in, Object unit, Object out) 
	{		
		FlowSet inval, outval;

		Map<String, Integer> mapp3 = new HashMap<String, Integer>();
		Map<String, Integer> mapp2, mapp; 
		
		inval = (FlowSet)in;   // entry set
		outval = (FlowSet)out; // output set
		Stmt u = (Stmt)unit;   // statment
				
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
	
		//Gen operation
		if( u instanceof AssignStmt && !u.containsInvokeExpr() )
		{ 
			Iterator<ValueBox> usIt = u.getUseBoxes().iterator();
			int a = 4, b = 4, c = 4;
			boolean flag1 = false, flag2 = false;
			while(usIt.hasNext())
			{
				ValueBox uBox = (ValueBox)usIt.next();

         		//System.out.println( "u: " + u );
         		//System.out.println( "uBox.getValue().toString(): " + uBox.getValue().toString() );
         		
				if( flag1==false )
				{
					 if( uBox.getValue() instanceof Local )
					 {
						  if( mapp2.containsKey(uBox.getValue().toString()) )
						  {
							  a = (int) mapp2.get(uBox.getValue().toString());
							  c = a;
							  flag1 = true;
						  }
					 }
					 else if( uBox.getValue() instanceof IntConstant )
					 {
						 String cons = uBox.getValue().toString();
					     a = Integer.parseInt(cons);
					     a = signed(a);
						 c = a;
					     flag1 = true;
					 }
					 else
					 {
						 c = a;
						 flag1 = true;
					 }
				}
				else if( flag2==false )
				{
					if( uBox.getValue() instanceof Local )
					{
						  if( mapp2.containsKey(uBox.getValue().toString()) )
						  {
							  b = (int) mapp2.get(uBox.getValue().toString());
							  c = b;
							  flag2 = true;
						  }
					}
				    else if( uBox.getValue() instanceof IntConstant )
					{
						 String cons = uBox.getValue().toString();
						 b = Integer.parseInt(cons);
						 b = signed(b);
						 c = b;
		   				 flag2 = true;
					}
				    else if( uBox.getValue().toString().contains("neg") )
				    {
				    	c = op2[a];
		   		        flag2 = true;
				    }
				}
				else 
				{
					if( uBox.getValue() instanceof AddExpr ) 
				    {
						c = op1[a][b];
				    }
					else
					{
					    c = 4;	
					}
			    }
			}
			if( flag1==true || flag2==true )
			{
        		//System.out.println( "flag1 flag2: " + flag1 + " " + flag2 );
				mapp.put((String)u.getDefBoxes().get(0).getValue().toString(), c);  				
			}
		}           
		if( u.containsInvokeExpr() )
		{
			String name = u.getInvokeExpr().getMethod().getName();
			List<Value> list = (u.getInvokeExpr().getArgs());

			HashMap<Integer, Integer> hhmm = map.get(name);
			
			if( !name.equals("parseInt") && !name.equals("println") && !name.equals("print") && !name.equals("<init>") )
			{
				int valluu;
				//System.out.println("name: " + name);
				//System.out.println("u: " + u);
				
				if( list.get(0) instanceof IntConstant )
				{
					valluu = signed(Integer.parseInt(list.get(0).toString()));
				}
				else
				{
					valluu = mapp.get(list.get(0).toString());
				}
				//System.out.println("valluu: " + valluu);
					
				int valu;
				if( hhmm.get(valluu)!=null )
				{
					valu = hhmm.get(valluu);
				}
				else
				{
					//System.out.println("Problem");
					valu = hhmm.get(valluu);
				}
								
				if( u.getDefBoxes().size()>0 )
				{
					mapp.put((String)u.getDefBoxes().get(0).getValue().toString(), valu);
				}
			}
			else if( name.equals("parseInt") )
			{
				int valu;
				
				if( mapp.get(list.get(0).toString())!=null )
				{
 					valu = mapp.get(list.get(0).toString());					
				}
				else
				{
					valu = Integer.parseInt(list.get(0).toString());
				}
				//System.out.println("valu: " + valu);
				
				if( u.getDefBoxes().size()>0 )
				{
					mapp.put((String)u.getDefBoxes().get(0).getValue().toString(), valu);
				}
			}
		}
			
		outval.add(mapp); 
		                                                 
		if( ( u instanceof AssignStmt || u instanceof ReturnStmt || u instanceof IfStmt ) && (printt==1) )
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
		if( u instanceof ReturnStmt )
		{
			Integer outcome = 0;
			String methodname = b.getMethod().getName();
			HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();
			
			Iterator<ValueBox> usIt = u.getUseBoxes().iterator();
			while(usIt.hasNext())
			{
				ValueBox uBox = (ValueBox)usIt.next();
			    if( mapp.get(uBox.getValue().toString())!=null )
			    {			    	
			       outcome = mapp.get(uBox.getValue().toString());
			    }
			    else
			    {
			       outcome = signed( Integer.parseInt(uBox.getValue().toString()) );	
			    }
			}
			             
			if( map.get(methodname) != null )
			{
			    hmap = map.get(methodname);
				if( hmap.get(sign)!=null )
				{
					if(hmap.get(sign)!=outcome)
					{
						hmap.put(sign, megee[outcome][hmap.get(sign)]);
					}
				}
				else
				{
					hmap.put(sign, outcome);
				}
			}
			else
			{
				hmap.put(sign, outcome);
			}
			
			System.out.println("Return: " + sign + " " + hmap.get(sign));
			map.put(methodname, hmap);
		}
	}
	
	public HashMap< String, HashMap< Integer, Integer > > getMap()
	{
		return this.map;
	}
	
	public Integer signed( Integer var )
	{
		if( var>0 )
			return 1;
		else if( var<0 )
			return 2;
		else
			return 3;
	}
	
	@Override
	protected void copy(Object source, Object dest) 
	{
		FlowSet srcSet = (FlowSet)source;
		FlowSet	destSet = (FlowSet)dest;
		srcSet.copy(destSet);	
	}
	
	@Override
	protected Object entryInitialFlow() 
	{
		Map<String, Integer> mapp = new HashMap<String, Integer>();
		FlowSet in1 = new ArraySparseSet();
	
		if(b.getMethod().isStatic())
		{
			int count = b.getMethod().getParameterCount();
			Iterator<Unit> j = b.getUnits().iterator();
			int i = 0;
			
			while(j.hasNext())
			{
				if(i<count)
				{
					Stmt stm = (Stmt)j.next();
					List<ValueBox> ls = stm.getDefBoxes();
					
					for(int k=0; k<(int)ls.size(); k++)
					{
						mapp.put(ls.get(k).getValue().toString(), sign);
					}
				}
				else
				{
					break;
				}
				i++;
			}
		}
		else
		{
			int count = b.getMethod().getParameterCount();
			Iterator<Unit> j = b.getUnits().iterator();
			int i = 0;
			
			while(j.hasNext())
			{
				Stmt stm = (Stmt)j.next();
				if(!stm.toString().contains("this"))
				{
					if(i<count)
					{	
						List<ValueBox> ls = stm.getDefBoxes();
						for(int k=0;k<ls.size();k++)
						{
							mapp.put(ls.get(k).getValue().toString(), sign);
						}
					}
					else
					{
	 					break;					
					}
					i++;
				}
			}
		}
		
		in1.add(mapp);
				
		return in1;
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
		
		for (Map.Entry<String, Integer> entry : mapp.entrySet())
		{			
 			if( mapp2.get( entry.getKey() )!=null )
				mapp3.put( entry.getKey(), megee[entry.getValue()][ mapp2.get(entry.getKey()) ] );   
			else if( mapp2.get( entry.getKey() )==null )
				mapp3.put( entry.getKey(), megee[entry.getValue()][entry.getValue()] );   
		}
		for (Map.Entry<String, Integer> entry : mapp2.entrySet())
		{
			if( mapp.get( entry.getKey() )!=null && ( mapp.get( entry.getKey() )==entry.getValue() ) )
				mapp3.put( entry.getKey(), megee[entry.getValue()][ mapp.get(entry.getKey()) ] );   
			else if( mapp.get( entry.getKey() )==null )
				mapp3.put( entry.getKey(), megee[entry.getValue()][entry.getValue()] );   
		}
		
		outSet.add(mapp3);
	}
	
	@Override
	protected Object newInitialFlow() 
	{	
		return new ArraySparseSet();
	}
}