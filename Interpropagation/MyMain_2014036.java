/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootResolver;
import soot.Transform;
import soot.jimple.toolkits.annotation.purity.DirectedCallGraph;
import soot.jimple.toolkits.annotation.purity.SootMethodFilter;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class MyMain 
{
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		if(args.length==0)
		{
			System.err.println("Usage: java Driver [options] classname");
			System.exit(0);
		}

		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("jb", "use-original-names:true");
		PackManager.v().getPack("wjtp").add( new Transform("wjtp.myTransform", new InterproceduralWrapper()) );
		SootResolver.v().resolveClass("java.lang.CloneNotSupportedException", SootClass.SIGNATURES);
		Options.v().set_output_format(Options.output_format_jimple);

		soot.Main.main(args);
	}
}