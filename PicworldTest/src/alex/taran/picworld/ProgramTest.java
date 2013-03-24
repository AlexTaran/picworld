/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.picworld;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;

public class ProgramTest{
	@Test
	public void TestSimpleProgram() {
		Map<String, List<Command> > code = new HashMap<String, List<Command>>();
		List<Command> main = new ArrayList<Command>();
		main.add(Command.ROTATE_LEFT);
		main.add(Command.MOVE_FORWARD);
		main.add(Command.ROTATE_RIGHT);
		
		code.put("main", main);
		Program p = new Program(code);
		
		p.beginExecution();
		Command[] expectedResult = new Command[]{Command.ROTATE_LEFT, Command.MOVE_FORWARD, Command.ROTATE_RIGHT};
		List<Command> cmds = new LinkedList<Command>();
		while (p.hasRemainingCommand()) {
			cmds.add(p.getCurrentCommand());
			p.nextCommand();
		}
		assertTrue(compareSequences(cmds, Arrays.asList(expectedResult)));
	}
	
	@Test
	public void TestSingleCall() {
		Map<String, List<Command> > code = new HashMap<String, List<Command>>();
		List<Command> main = new ArrayList<Command>();
		main.add(Command.CALL_A);
		List<Command> procA = new ArrayList<Command>();
		procA.add(Command.ROTATE_LEFT);
		procA.add(Command.MOVE_FORWARD);
		procA.add(Command.ROTATE_RIGHT);
		
		code.put("main", main);
		code.put(Command.CALL_A.getProcName(), procA);
		Program p = new Program(code);
		
		p.beginExecution();
		Command[] expectedResult = new Command[]{Command.ROTATE_LEFT, Command.MOVE_FORWARD, Command.ROTATE_RIGHT};
		List<Command> cmds = new LinkedList<Command>();
		while (p.hasRemainingCommand()) {
			cmds.add(p.getCurrentCommand());
			p.nextCommand();
		}
		assertTrue(compareSequences(cmds, Arrays.asList(expectedResult)));
	}
	
	@Test
	public void TestDoubleCall() {
		Map<String, List<Command> > code = new HashMap<String, List<Command>>();
		List<Command> main = new ArrayList<Command>();
		main.add(Command.CALL_A);
		main.add(Command.CALL_A);
		List<Command> procA = new ArrayList<Command>();
		procA.add(Command.ROTATE_LEFT);
		procA.add(Command.MOVE_FORWARD);
		procA.add(Command.ROTATE_RIGHT);
		
		code.put("main", main);
		code.put(Command.CALL_A.getProcName(), procA);
		Program p = new Program(code);
		
		p.beginExecution();
		Command[] expectedResult = new Command[]{
				Command.ROTATE_LEFT, Command.MOVE_FORWARD, Command.ROTATE_RIGHT,
				Command.ROTATE_LEFT, Command.MOVE_FORWARD, Command.ROTATE_RIGHT};
		List<Command> cmds = new LinkedList<Command>();
		while (p.hasRemainingCommand()) {
			cmds.add(p.getCurrentCommand());
			p.nextCommand();
		}
		assertTrue(compareSequences(cmds, Arrays.asList(expectedResult)));
	}
	
	@Test
	public void TestDeepCall() {
		Map<String, List<Command> > code = new HashMap<String, List<Command>>();
		List<Command> main = new ArrayList<Command>();
		main.add(Command.CALL_B);
		List<Command> procA = new ArrayList<Command>();
		procA.add(Command.ROTATE_LEFT);
		procA.add(Command.MOVE_FORWARD);
		procA.add(Command.ROTATE_RIGHT);
		List<Command> procB = new ArrayList<Command>();
		procB.add(Command.CALL_A);
		
		code.put("main", main);
		code.put(Command.CALL_A.getProcName(), procA);
		code.put(Command.CALL_B.getProcName(), procB);
		Program p = new Program(code);
		
		p.beginExecution();
		Command[] expectedResult = new Command[]{
				Command.ROTATE_LEFT, Command.MOVE_FORWARD, Command.ROTATE_RIGHT};
		List<Command> cmds = new LinkedList<Command>();
		while (p.hasRemainingCommand()) {
			cmds.add(p.getCurrentCommand());
			p.nextCommand();
		}
		assertTrue(compareSequences(cmds, Arrays.asList(expectedResult)));
	}
	
	private boolean compareSequences(List<Command> l1, List<Command> l2) {
		if (l1.size() != l2.size()) {
			return false;
		}
		Iterator<Command> i1 = l1.iterator();
		Iterator<Command> i2 = l1.iterator();
		while(i1.hasNext()) {
			if (!i1.next().equals(i2.next())) {
				return false;
			}
		}
		return true;
	}
}
