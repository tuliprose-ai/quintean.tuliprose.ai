package ai.tuliprose.libs.quintean;

import java.util.Arrays;

public enum Quintean {
    VERY_HIGH (95, 100, new boolean[] {true, true, true}),
    HIGH (82, 94, new boolean[] {true, false, true}),
    MEDIUM (19, 81, new boolean[] {true, false, true}),
    LOW (6, 18, new boolean[] {false, true, false}),
    VERY_LOW (0, 5, new boolean[] {false, false, false});

    private final int minValue; // Minimum value of the enum value
    private final int maxValue; // Maximum value of the enum value
    private final int midValue; // Midpoint of the range
    private final boolean[] bitset;
    
    Quintean(int minValue, int maxValue, boolean[] bitset) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.midValue = (minValue + maxValue) / 2;
        this.bitset = bitset;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMidValue() {
        return midValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public boolean[] getBitset() {
    	return bitset;
    }
    
    public boolean isInRange(int value) {
        return value >= minValue && value <= maxValue;
    }

    public boolean canExpand() {
        return shouldExpand() || mustExpand();
    }
    public boolean shouldExpand() {
        return this == HIGH || this == LOW;
    }

    public boolean mustExpand() {
        return this == MEDIUM;
    }

    public boolean canStabilize() {
        return this != MEDIUM;
    }

    public boolean shouldStabilize() {
        return this == HIGH || this == LOW;
    }

    public boolean mustStabilize() {
        return this == VERY_HIGH || this == VERY_LOW;
    }

    public Quintean stabilize() {
        return this == VERY_HIGH ? HIGH :
                this == HIGH ? MEDIUM :
                this == MEDIUM ? MEDIUM :
                this == LOW ? MEDIUM : LOW;
    }

    public boolean canGrow() {
        return this != VERY_HIGH;
    }

    public Quintean grow() {
        return this == VERY_LOW ? LOW :
                this == LOW ? MEDIUM :
                this == MEDIUM ? HIGH :
                VERY_HIGH;
    }

    public boolean canShrink() {
        return this != VERY_LOW;
    }

    public Quintean shrink() {
        return this == VERY_HIGH ? HIGH :
                this == HIGH ? MEDIUM :
                this == MEDIUM ? LOW :
                VERY_LOW;
    }

    public boolean canActivate() {
        return this != VERY_LOW && this != VERY_HIGH;
    }
    
    public Quintean activate() {
        return this == LOW ? MEDIUM :
                this == HIGH ? MEDIUM :
                this == MEDIUM ? HIGH : null;
    }

    public boolean canDeactivate() {
        return this != VERY_LOW && this != VERY_HIGH;
    }
    
    public Quintean deactivate() {
        return this == LOW ? MEDIUM :
                this == HIGH ? MEDIUM :
                this == MEDIUM ? LOW : null;
    }
    
    public static Quintean getRandom() {
        return Quintean.values()[(int) (Math.random() * Quintean.values().length)];
    }

	public static Quintean valueOf(boolean[] bitset) {
        for (Quintean quintean : Quintean.values()) {
            if (Arrays.equals(quintean.getBitset(), bitset)) {
            	return quintean;
            }
        }
        
        return MEDIUM; // Default value if no match is found
	}
}
