/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.picworld;

public enum Command {
	MOVE_FORWARD,
	ROTATE_LEFT,
	ROTATE_RIGHT,
	LIGHT_ON,
	LIGHT_OFF,
	TOGGLE_LIGHT,
	CALL_A,
	CALL_B,
	JUMP,
	SHOOT;
	
	public boolean isCall() {
		return this == CALL_A || this == CALL_B;
	}
	
	public String getProcName(){
		if (!isCall()) {
			throw new RuntimeException("Call getProcName of non-call-proc command");
		}
		if (this == CALL_A) {
			return "A";
		} else if (this == CALL_B) {
			return "B";
		} else {
			throw new RuntimeException("Couldn't get proc name of call-command. Please, see source.");
		}
	}
}
