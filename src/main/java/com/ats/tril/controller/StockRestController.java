package com.ats.tril.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ats.tril.model.ErrorMessage;
import com.ats.tril.model.GetCurrentStock;
import com.ats.tril.model.GetItem;
import com.ats.tril.model.ItemListWithCurrentStock;
import com.ats.tril.model.MinAndRolLevelReport;
import com.ats.tril.model.OpeningStockModel;
import com.ats.tril.model.StockDetail;
import com.ats.tril.model.StockHeader;
import com.ats.tril.repository.AddOpeningStock;
import com.ats.tril.repository.GetItemRepository;
import com.ats.tril.repository.ItemListWithCurrentStockRepository;
import com.ats.tril.repository.MinAndRolLevelReportRepository;
import com.ats.tril.repository.stock.GetCurrentStockHeaderRepository;
import com.ats.tril.repository.stock.StockDetailRepository;
import com.ats.tril.repository.stock.StockHeaderRepository; 

@RestController
public class StockRestController {
	
	
	@Autowired
	StockHeaderRepository stockHeaderRepository;

	@Autowired
	StockDetailRepository stockDetailRepository;
	
	@Autowired
	GetCurrentStockHeaderRepository getCurrentStockHeaderRepository;
	
	@Autowired
	GetItemRepository getItemRepository;
	
	@Autowired
	MinAndRolLevelReportRepository minAndRolLevelReportRepository;
	
	@Autowired
	ItemListWithCurrentStockRepository itemListWithCurrentStockRepository;
	
	@Autowired
	AddOpeningStock addOpeningStock;

	@RequestMapping(value = { "/insertStock" }, method = RequestMethod.POST)
	public @ResponseBody StockHeader insertStock(@RequestBody StockHeader stockHeader) {
		
		StockHeader save = new StockHeader();

		try {

			save = stockHeaderRepository.saveAndFlush(stockHeader);

			for (int i = 0; i < stockHeader.getStockDetailList().size(); i++)
				stockHeader.getStockDetailList().get(i).setStockHeaderId(save.getStockHeaderId());

			List<StockDetail> StockDetails = stockDetailRepository.saveAll(stockHeader.getStockDetailList());
			 
			save.setStockDetailList(StockDetails);
		} catch (Exception e) {

			e.printStackTrace();

		}
		return save;

	}
	
	@RequestMapping(value = { "/getCurrentStock" }, method = RequestMethod.POST)
	public @ResponseBody List<GetCurrentStock> getCurrentStock(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate) {
		
		List<GetCurrentStock> getCurrentStock = new ArrayList<GetCurrentStock>();

		try {

			getCurrentStock = getCurrentStockHeaderRepository.getCurrentStock(fromDate,toDate);
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return getCurrentStock;

	}
	
	@RequestMapping(value = { "/getCurrentStockByItemId" }, method = RequestMethod.POST)
	public @ResponseBody GetCurrentStock getCurrentStockByItemId(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,@RequestParam("itemId") int itemId) {
		
		 GetCurrentStock  getCurrentStock = new  GetCurrentStock ();

		try {

			getCurrentStock = getCurrentStockHeaderRepository.getCurrentStockByItemId(fromDate,toDate,itemId);
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return getCurrentStock;

	}
	
	@RequestMapping(value = { "/getCurrentRunningMonthAndYear" }, method = RequestMethod.GET)
	public @ResponseBody StockHeader getCurrentRunningMonthAndYear() {
		
		StockHeader getCurrentRunningMonthAndYear = new StockHeader();

		try {

			getCurrentRunningMonthAndYear = stockHeaderRepository.findByStatusAndDelStatus(0,1);
			
			if(getCurrentRunningMonthAndYear==null)
			{
				getCurrentRunningMonthAndYear = new StockHeader();
			}
 
			 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return getCurrentRunningMonthAndYear;

	}
	
	@RequestMapping(value = { "/getStockDetailByStockId" }, method = RequestMethod.POST)
	public @ResponseBody List<StockDetail> getStockDetailByStockId(@RequestParam("stockId") int stockId) {
		
		List<StockDetail> StockDetails = new ArrayList<>();

		try {
 
			 StockDetails = stockDetailRepository.findByStockHeaderIdAndDelStatus(stockId,1);
			  
		} catch (Exception e) {

			e.printStackTrace();

		}
		return StockDetails;

	}
	
	@RequestMapping(value = { "/getItemListLessThanROL" }, method = RequestMethod.POST)
	public @ResponseBody List<GetCurrentStock> getItemListLessThanROL(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate) {
		
		List<GetCurrentStock> getCurrentStock = new ArrayList<GetCurrentStock>();

		try {
			
			List<GetItem>  itemList = getItemRepository.getAllItems();

			getCurrentStock = getCurrentStockHeaderRepository.getCurrentStock(fromDate,toDate);
			
			for(int i = 0 ; i<itemList.size() ; i++)
			{
				for(int j = 0 ; j<getCurrentStock.size() ; j++)
				{
					if(itemList.get(i).getItemId()==getCurrentStock.get(j).getItemId())
					{
						if(itemList.get(i).getItemRodLevel()<(getCurrentStock.get(j).getOpeningStock()+getCurrentStock.get(j).getApproveQty()-
									getCurrentStock.get(j).getIssueQty()+getCurrentStock.get(j).getReturnIssueQty()-getCurrentStock.get(j).getDamageQty()-
									getCurrentStock.get(j).getGatepassQty()+getCurrentStock.get(j).getGatepassReturnQty()))
							
						{
							getCurrentStock.remove(j);
						}
						else
						{
							getCurrentStock.get(j).setRolLevel(itemList.get(i).getItemRodLevel());
						}
						
						break;
					} 
				}
			}
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return getCurrentStock;

	}
	
	@RequestMapping(value = { "/getStockBetweenDateWithCatId" }, method = RequestMethod.POST)
	public @ResponseBody List<GetCurrentStock> getStockBetweenDateWithCatId(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,@RequestParam("catId") int catId) {
		
		List<GetCurrentStock> getCurrentStock = new ArrayList<GetCurrentStock>();

		try {

			getCurrentStock = getCurrentStockHeaderRepository.getStockBetweenDateWithCatId(fromDate,toDate,catId);
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return getCurrentStock;

	}
	
	@RequestMapping(value = { "/getStockBetweenDateWithCatIdAndTypeId" }, method = RequestMethod.POST)
	public @ResponseBody List<GetCurrentStock> getStockBetweenDateWithCatIdAndTypeId(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,@RequestParam("catId") int catId,@RequestParam("typeId") int typeId) {
		
		List<GetCurrentStock> getCurrentStock = new ArrayList<GetCurrentStock>();

		try {

			if(typeId==0) {
			 getCurrentStock = getCurrentStockHeaderRepository.getStockBetweenDateWithCatId(fromDate,toDate,catId);
			}
			else {
			 getCurrentStock = getCurrentStockHeaderRepository.getStockBetweenDateWithCatId(fromDate,toDate,catId,typeId);
			}
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return getCurrentStock;

	}
	
	@RequestMapping(value = { "/minQtyAndRolQtyLevelReport" }, method = RequestMethod.POST)
	public @ResponseBody List<MinAndRolLevelReport> minQtyAndRolQtyLevelReport(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,@RequestParam("catId") int catId) {
		
		List<MinAndRolLevelReport> getCurrentStock = new ArrayList<MinAndRolLevelReport>();

		try {

			if(catId!=0) {
				getCurrentStock = minAndRolLevelReportRepository.getCurrentStock(fromDate,toDate,catId);
			}
			else {
				getCurrentStock = minAndRolLevelReportRepository.getCurrentStock(fromDate,toDate);
			}
			
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return getCurrentStock;

	}
	
	@RequestMapping(value = { "/getItemListByCatIdWithStock" }, method = RequestMethod.POST)
	public @ResponseBody List<ItemListWithCurrentStock> getItemListByCatIdWithStock(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,@RequestParam("catId") int catId) {
		
		List<ItemListWithCurrentStock> list = new ArrayList<>();

		try {

			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("dd");
			SimpleDateFormat yy = new SimpleDateFormat("yyyy");
			SimpleDateFormat mm = new SimpleDateFormat("MM");
			
			int days =  Integer.parseInt(sf.format(date));
			String year =   yy.format(date);
			int month =  Integer.parseInt(mm.format(date));
			String firstDate = year+"-"+month+"-01";
			
			list = itemListWithCurrentStockRepository.getItemListByCatIdWithStock(firstDate,fromDate,toDate,catId);
			 
			for(int i = 0; i < list.size() ; i++) {
				
				list.get(i).setClsQty(list.get(i).getOpeningStock()+list.get(i).getApproveQty()-list.get(i).getIssueQty()
						+list.get(i).getIssueReturnQty()-list.get(i).getDamageQty());
				list.get(i).setAvgIssueQty(list.get(i).getIssueQtyAvg()/days);
			}
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return list;

	}
	
	@RequestMapping(value = { "/getItemListByItemIdWithStock" }, method = RequestMethod.POST)
	public @ResponseBody  ItemListWithCurrentStock  getItemListByItemIdWithStock(@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,@RequestParam("itemId") int itemId) {
		
		 ItemListWithCurrentStock  itemListWithCurrentStock = new ItemListWithCurrentStock();

		try {
			
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("dd");
			SimpleDateFormat yy = new SimpleDateFormat("yyyy");
			SimpleDateFormat mm = new SimpleDateFormat("MM");
			
			int days =  Integer.parseInt(sf.format(date));
			String year =   yy.format(date);
			int month =  Integer.parseInt(mm.format(date));
			String firstDate = year+"-"+month+"-01";

			itemListWithCurrentStock = itemListWithCurrentStockRepository.getItemListByItemIdWithStock(firstDate,fromDate,toDate,itemId);
			
			 
			itemListWithCurrentStock.setClsQty(itemListWithCurrentStock.getOpeningStock()+itemListWithCurrentStock.getApproveQty()-itemListWithCurrentStock.getIssueQty()
						+itemListWithCurrentStock.getIssueReturnQty()-itemListWithCurrentStock.getDamageQty());
			itemListWithCurrentStock.setAvgIssueQty(itemListWithCurrentStock.getIssueQtyAvg()/days);
			 
 
		} catch (Exception e) {

			e.printStackTrace();

		}
		return itemListWithCurrentStock;

	}
	
	@RequestMapping(value = { "/getAllitemOpeningStock" }, method = RequestMethod.POST)
	public @ResponseBody List<OpeningStockModel> getAllitemOpeningStock(@RequestParam("cat_id") int cat_id,@RequestParam("vendId") int vendId) {

		List<OpeningStockModel> OpeningStockModel = new ArrayList<OpeningStockModel>();
//System.out.println("Inside getAllitemOpeningStock");
		try {

			OpeningStockModel = addOpeningStock.getIteminfoOPeningStock(cat_id,vendId);

		} catch (Exception e) {

			e.printStackTrace();

		}
		return OpeningStockModel;

	}
	
	@RequestMapping(value = { "/getStateCodeByVendId" }, method = RequestMethod.POST)
	public @ResponseBody ErrorMessage getStateCodeByVendId(@RequestParam("vendId") int vendId) {

		ErrorMessage errmsg = new ErrorMessage();
//System.out.println("Inside getAllitemOpeningStock");
		try {

			String code = addOpeningStock.getStateCodeByVendId(vendId);
			errmsg.setError(false);
			errmsg.setMessage(code);

		} catch (Exception e) {

			e.printStackTrace();

		}
		return errmsg;

	}

}
