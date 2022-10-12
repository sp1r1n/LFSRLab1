import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LFSR {
    // Variables provided by constructor.
    public final int bits, mask, seed, bitsMask;

    // Current value, increased when LFSR is incremented.
    private int current, next;

    // Constructor with default seed (1).
    public LFSR(int bits, int mask) {
        this(bits, mask, 1);
    }

    // Constructor with custom seed.
    public LFSR(int bits, int mask, int seed) {
        // Set final variables.
        this.bits = bits;
        this.bitsMask = (0x01 << bits) - 1;
        this.mask = mask & bitsMask;
        this.seed = seed & bitsMask;

        // Make sure this is a valid LFSR.
        if (this.seed == 0) {
            System.out.println("Seed of 0 will always produce 0");
        }
        if (this.mask == 0) {
            System.out.println("Mask of 0 will break generator");
        }
        // Set up current and next values.
        this.current = this.seed;
        this.next = next(this.seed, this.bits, this.bitsMask, this.mask);
    }

    public int next() {
        this.current = this.next;
        this.next = next(this.current, this.bits, this.bitsMask, this.mask);
        return this.current;
    }

    static public int next(int in, int bits, int bitsMask, int mask) {
        return (in << 1) & bitsMask | lsb(in, bits, bitsMask, mask);
    }

    static public int lsb(int value, int bits, int bitsMask, int mask) {
        int bit = 0;
        for (int i = 0x01; i < bitsMask; i <<= 1) {
            if ((mask & i) != 0 && (value & i) != 0) {
                bit ^= 0x01;
            }
        }
        return bit;
    }


    public static void main(String[] args) throws IOException {
        int[] masks;
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter path to file: ");
        String path = br1.readLine();

        BufferedReader br2 = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br2.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br2.readLine();
            }
            String everything = sb.toString();
            String[] items = everything.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").replaceAll("\\(", "").replaceAll("\\)", "").split(",");

            masks = new int[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    masks[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {
                    System.err.println("Invalid Format! Use , as separator and avoid special characters");
                }
            }

        } finally {
            br2.close();
        }

        // Instantiate LFSR number generators.
        LFSR[] lfsr = new LFSR[masks.length];

        for (int i = 0; i < masks.length; i++) {
            lfsr[i] = new LFSR(8, masks[i]);
        }

        // Print a top line that shows our masks.
        System.out.print("     ");
        for (int i = 0; i < masks.length; i++) {
            System.out.printf(" %02X ", masks[i]);
        }
        System.out.println("\n" +
                "---------------------------------------------------------------------");

        // Print all results from our LFSR.
        for (int j = 0; j <= 254; j++) {
            System.out.printf("%3d | ", j + 1);
            for (int i = 0; i < lfsr.length; i++) {
                int val = lfsr[i].next();
                System.out.printf("%3d ", val);
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }
}
