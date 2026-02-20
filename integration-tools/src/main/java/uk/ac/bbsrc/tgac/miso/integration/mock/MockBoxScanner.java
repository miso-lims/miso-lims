package uk.ac.bbsrc.tgac.miso.integration.mock;


import org.springframework.stereotype.Component;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.integration.BoxScan;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

import java.net.http.HttpClient;
import java.util.*;

public class MockBoxScanner implements BoxScanner {
    private int rows = 8;
    private int cols = 12;

    @Override
    public void prepareScan(int expectedRows, int expectedColums) throws IntegrationException {
        this.rows = expectedRows;
        this.cols = expectedColums;

        try{
            Thread.sleep(500);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public BoxScan getScan() throws IntegrationException {
        try{
            Thread.sleep(12000);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        return new MockScan(rows,cols);
    }

    private static class MockScan implements BoxScan {
        private final int rows;
        private final int cols;
        private final Map<String, String> barcodes = new HashMap<>();

        public MockScan(int rows, int cols){
            this.rows = rows;
            this.cols = cols;
            generateBarcodes();
        }

        private void generateBarcodes() {
            for(int r = 0; r<rows; r++) {
                for (int c = 0; c< cols; c++){
                    String pos = BoxUtils.getPositionString(r,c);
                    barcodes.put(pos, "Mock -" + pos + " - " + System.currentTimeMillis() % 100);
                }
            }
        }

        @Override
        public String getBarcode(String position){
            return barcodes.get(position);
        }

        @Override
        public String getBarcode(char row, int column) {
            return getBarcode(BoxUtils.getPositionString(row, column));
        }

        @Override
        public String getBarcode(int row, int column) {
            return getBarcode(BoxUtils.getPositionString(row,column));
        }

        @Override
        public Map<String, String> getBarcodesMap() {
            Map<String, String> copy = new HashMap<>();
            for(Map.Entry<String, String> entry: barcodes.entrySet()){
                String val = entry.getValue();
                if(val == null){
                    val = getNoTubeLabel();
                }
                else if(val.isEmpty()) {
                    val = getNoReadLabel();
                }
                copy.put(entry.getKey(),val);
            }
            return copy;
        }

        @Override
        public boolean isFull() {
            return barcodes.values().stream().noneMatch(val -> val == null);
        }

        @Override
        public boolean isEmpty() {
            return barcodes.values().stream().allMatch(val -> val == null);
        }

        @Override
        public int getMaximumTubeCount() {
            return rows * cols ;
        }

        @Override
        public int getTubeCount() {
            return (int) barcodes.values().stream().filter(val -> val != null).count();
        }

        @Override
        public boolean hasReadErrors() {
            return barcodes.values().stream().anyMatch(val -> val != null && val.isEmpty());
        }

        @Override
        public List<String> getReadErrorPositions() {
            List<String> errors = new ArrayList<>();
            for(Map.Entry<String, String> entry: barcodes.entrySet()){
                if(entry.getValue() !=  null && entry.getValue().isEmpty()){
                    errors.add(entry.getKey());
                }
            }
            return errors;
        }

        @Override
        public int getRowCount() {
            return  rows;
        }

        @Override
        public int getColumnCount(){
            return cols;
        }

        @Override
        public String getNoReadLabel(){
            return "No Read";
        }

        @Override
        public String getNoTubeLabel(){
            return "No Tube";
        }
    }
}
