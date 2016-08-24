package scheduling_solution.visualisation;

import java.awt.Color;

public class ColourArray {
	
	Color[] colourArray;
	
	public ColourArray() {
		this.colourArray = new Color[31];
		
		// Hard coding in the hex values for the colours that are being used
		colourArray[0] = new Color(0x00FF00); // Green
		colourArray[1] = new Color(0x11FF00);
		colourArray[2] = new Color(0x22FF00);
		colourArray[3] = new Color(0x33FF00);
		colourArray[4] = new Color(0x44FF00);
		colourArray[5] = new Color(0x55FF00);
		colourArray[6] = new Color(0x66FF00);
		colourArray[7] = new Color(0x77FF00);
		colourArray[8] = new Color(0x88FF00);
		colourArray[9] = new Color(0x99FF00);
		colourArray[10] = new Color(0xAAFF00);
		colourArray[11] = new Color(0xBBFF00);
		colourArray[12] = new Color(0xCCFF00);
		colourArray[13] = new Color(0xDDFF00);
		colourArray[14] = new Color(0xEEFF00);
		colourArray[15] = new Color(0xFFFF00); // Yellow
		colourArray[16] = new Color(0xFFEE00);
		colourArray[17] = new Color(0xFFDD00);
		colourArray[18] = new Color(0xFFCC00);
		colourArray[19] = new Color(0xFFBB00);	
		colourArray[20] = new Color(0xFFAA00);
		colourArray[21] = new Color(0xFF9900);
		colourArray[22] = new Color(0xFF8800);
		colourArray[23] = new Color(0xFF7700);
		colourArray[24] = new Color(0xFF6600);
		colourArray[25] = new Color(0xFF5500);
		colourArray[26] = new Color(0xFF4400);
		colourArray[27] = new Color(0xFF3300);
		colourArray[28] = new Color(0xFF2200);
		colourArray[29] = new Color(0xFF1100);
		colourArray[30] = new Color(0xFF0000); // Red
	}

	protected Color getColour(int index) {
		return colourArray[index];
	}
}
