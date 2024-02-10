package team1502.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MdFormatter {
    // class Table extends MdFormatter {
    //     String title;
        
    //     Table(String title) { this.title = title;}
        
    // }
    
    class Entry {
        String text;
        Boolean rightJustify = false;
        Entry(String text) {this.text = text != null ? text : "";}
        Entry(int text) {this.text = Integer.toString(text); rightJustify = true;}

        int Width() {return text.length();}
        String Text() {return text;}

        String FormatText(int width) {
            if (text == "---") { // heading separator, fill don't pad
                text = new String(new char[width]).replace('\0', '-');
            }
            var padding = width - this.Width();
            if (padding < 1) return text;
            //System.out.println(text + padding + " | ");
            if (this.rightJustify) {
                return new String(new char[padding]).replace('\0', ' ') + text;
            } else {
                return text + new String(new char[padding]).replace('\0', ' ');
            }
        }
    }

    // class IntEntry extends Entry {
    //     Entry
    // }

    class Row {
        private Entry[] entries;
        private int[] widths;
        int columns = 0;

        // Row(Entry entry1) {entries = new Entry[]{entry1}; columns = 1; widths = new int[]{entry1.Width()};}
        // Row(Entry entry1, Entry entry2) {entries = new Entry[]{entry1, entry2}; columns = 2; widths = new int[]{entry1.Width(), entry2.Width()};}

        Row(Entry... entry) {
            entries = entry;
            columns = entries.length;
            widths = Arrays.stream(entries).mapToInt(e->e.Width()).toArray();
        }

        int[] Widths() { return widths; } //Arrays.stream(entries).mapToInt(e -> e.Width()).toArray();}
        
        String EntryText(int[] widths, String sep) {
            return String.join(sep, IntStream.range(0, widths.length).boxed().map(i -> entries[i].FormatText(widths[i])).toList());
        }

        // String EntryText(int i, int width) {
        //     var entry = entries[i];
        //     var padding = width - entry.Width();
        //     if (padding < 1) return entry.text;
        //     System.out.println(entry.text + padding + " | ");
        //     if (entry.rightJustify) {
        //         return new String(new char[padding]).replace('\0', ' ') + entry.text;
        //     } else {
        //         return entry.text + new String(new char[padding]).replace('\0', ' ');
        //     }
        // }
    }

    class Rows {
        int columns = 0;
        int[] widths;
        List<Row> rows = new ArrayList<>();

        void Add(Row row) {
            columns = Math.max(columns, row.columns);
            rows.add(row);

            if (widths == null) {
                widths = row.Widths();
            } else {
                for(var i = 0; i < columns; i++) {
                    widths[i] = Math.max(widths[i], row.entries[i].Width());
                }
            }
        }
        
        Iterable<String> AsTable(String sep) {
            return rows.stream().map(r -> r.EntryText(widths, sep)).toList();
        }
    }

    String title;
    private Rows rows;
    //private MdFormatter current;

    public static MdFormatter Table(String title) {

        var formatter = new MdFormatter();
        formatter.title = title;
        return formatter;
    }

    MdFormatter AddRow(Row row) {
        if (rows == null) {rows = new Rows();}
        rows.Add(row);
        return this;
    }

    MdFormatter AddRow(String... text) {
        var entries = Arrays.stream(text).map(t->new Entry(t)).toArray(Entry[]::new);
        return AddRow(new Row(entries));
    }
    
    // MdFormatter AddRow(String text, String string2) {
    //     return AddRow(new Row(new Entry(text), new Entry(string2)));
    // }
    MdFormatter AddRow(String text, int number) {
        return AddRow(new Row(new Entry(text), new Entry(number)));
    }

    Iterable<String> AsTable() {
        List<String> section = new ArrayList<>();
        section.add("# " + title);
        section.add("");
        rows.AsTable(" | ").forEach(row -> section.add(row));
        section.add("");
        return section;
        //return rows.rows.stream().map(Row::toString).toList();
    }

    public MdFormatter Heading(String... text) {
        AddRow(text);
        var seps = Arrays.stream(text).map(t->"---").toArray(String[]::new);
        return AddRow(seps);

    }
    // public MdFormatter Heading(String string, String string2) {
    //     AddRow(string, string2);
    //     return AddRow("---", "---");
    // }
}
