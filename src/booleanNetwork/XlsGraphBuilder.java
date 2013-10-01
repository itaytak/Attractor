/**
 * 
 */
package booleanNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import utils.BooleanFunction;
import utils.NamedBoolean;
import utils.TruthRow;

/**
 * @author Itay
 *
 */
public class XlsGraphBuilder implements GraphBuilder {

	String fileName;
	Integer sheetNumber;
	Graph graph;
	
	/**
	 * @param fileName
	 * @param sheetNumber
	 */
	public XlsGraphBuilder(String fileName, Integer sheetNumber) {
		super();
		this.fileName = new String(fileName);
		this.sheetNumber = new Integer(sheetNumber);
		graph = new Graph();
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @return the sheetNumber
	 */
	public Integer getSheetNumber() {
		return sheetNumber;
	}
	/* (non-Javadoc)
	 * @see booleanNetwork.GraphBuilder#buildGraph()
	 */
	@Override
	public Graph buildGraph() {
		try {
			Node graphNode = null;
			FileInputStream file = new FileInputStream(new File(this.fileName));
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(this.sheetNumber);
			Iterator<Row> rowIterator = sheet.rowIterator();
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				if(cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					Node tempNode = createNewNode(cell, cellIterator);
					if(tempNode != null) {
						graphNode = tempNode;
						this.graph.addNode(graphNode);
					} else {
						if(graphNode == null) {
							throw new IOException("Found orphan function");
						}
						TruthRow tr = createBooleanFunction(cell, cellIterator, graphNode);
						graphNode.addConditionsRow(tr);	
					}
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return graph;
	}
	
	private Node createNewNode(Cell cell, Iterator<Cell> cellIterator) throws IOException {
		Node res = null;
		ArrayList<String> neighbours = new ArrayList<String>();
		validateCellType(cell);
		String cellValue = cell.getStringCellValue();
		String nodeName = null;
		if(cellValue.endsWith("*")) {
			nodeName = cellValue.replaceFirst("[*]", "");
			while(cellIterator.hasNext()) {
				Cell neighbourCell = cellIterator.next();
				if(isBlankCellType(neighbourCell)) {
					break;
				}
				cellValue = neighbourCell.getStringCellValue();
				neighbours.add(cellValue);
			}
			if(nodeName != null) {
				Collections.sort(neighbours);
				res = new Node(nodeName, null, neighbours);
			}
		}
		
		return res;
	}
	
	private TruthRow createBooleanFunction(Cell firstCell, Iterator<Cell> cellIterator, Node father) throws IOException {
		TruthRow tr = new BooleanFunction(father.getName(), true);
		NamedBoolean nb = createNamedBoolean(firstCell, cellIterator);
		tr.addCondition(nb);
		
		while(cellIterator.hasNext()) {
			nb = createNamedBoolean(null, cellIterator);
			if(nb.getName() != null && nb.getValue() != null) {
				tr.addCondition(nb);
			}
		}
		
		return tr;
	}
	
	private NamedBoolean createNamedBoolean(Cell firstCell, Iterator<Cell> cellIterator) throws IOException {
		NamedBoolean nb = new NamedBoolean();
		
		if(firstCell != null) {
			nb = updateNamedBoolean(nb, firstCell, true);
		}
		while(nb.getName() == null && cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if(!isBlankCellType(cell)) {
				nb = updateNamedBoolean(nb, cell, false);
			} else {
				break;
			}
			
		}
		
		return nb;
	}
	
	private NamedBoolean updateNamedBoolean(NamedBoolean nb, Cell cell, boolean isFirstCell) throws IOException {
		NamedBoolean res = nb;
		
		validateCellType(cell);
		String cellValue = cell.getStringCellValue();
		
		if(cellValue.equals("and")) {
			if(isFirstCell) {
				throw new IOException("Found \'and\' in first cell");
			} 
		} else if(cellValue.equals("not")) {
			if(res.getValue() != null && res.getValue() == false){
				throw new IOException("Found consecutive \'not\'");
			} 
			res.setValue(false);
			
		} else {
			res.setName(cellValue);
			if(res.getValue() == null) {
				res.setValue(true);
			}
		}
		
		return res;
	}
	
	private void validateCellType(Cell cell) throws IOException{
		if(cell.getCellType() != Cell.CELL_TYPE_STRING) {
			throw new IOException("Illegal cell type");
		} 
	}
	
	private boolean isBlankCellType(Cell cell) {
		boolean isBlankCellType = cell.getCellType() == Cell.CELL_TYPE_BLANK;
		return isBlankCellType;
	}
	
}
