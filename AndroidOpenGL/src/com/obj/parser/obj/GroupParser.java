package com.obj.parser.obj;

import com.obj.Group;
import com.obj.WavefrontObject;
import com.obj.parser.LineParser;
import java.util.Random;

public class GroupParser extends LineParser {
	Group newGroup = null;
	
	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		
		if (wavefrontObject.getCurrentGroup() != null)
			wavefrontObject.getCurrentGroup().pack();
		while(wavefrontObject.getGroupsDirectAccess().containsKey(newGroup.getName())) {
			newGroup.setName(newGroup.getName() + "1");
		}
		wavefrontObject.getGroups().add(newGroup);
		wavefrontObject.getGroupsDirectAccess().put(newGroup.getName(),newGroup);
		
		wavefrontObject.setCurrentGroup(newGroup);
	}

	@Override
	public void parse() {
		String groupName = "UnknownGroup";
		if (words.length > 1) {
			groupName = words[1];
		}
		newGroup = new Group(groupName);
	}

}
