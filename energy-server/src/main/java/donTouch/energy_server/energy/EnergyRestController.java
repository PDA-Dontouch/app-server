package donTouch.energy_server.energy;

import donTouch.energy_server.energy.dto.EnergyFundDetailDto;
import donTouch.energy_server.energy.dto.EnergyFundDto;
import donTouch.energy_server.energy.service.EnergyFundDetailService;
import donTouch.energy_server.energy.service.EnergyFundService;
import donTouch.utils.utils.ApiUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class EnergyRestController {
    private EnergyFundService energyFundService;
    private EnergyFundDetailService energyFundDetailService;

    @GetMapping("/api/energy")
    public ApiUtils.ApiResult<List<EnergyFundDto>> getAllEnergy(){
        try{
            List<EnergyFundDto> resultList = energyFundService.getAllEnergyFund();
            return ApiUtils.success(resultList);
        }catch (NullPointerException e){
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/api/energy/{energyId}")
    public ApiUtils.ApiResult<EnergyFundDetailDto> getEnergyDetail(@PathVariable String energyId){
        try{
            EnergyFundDetailDto result = energyFundDetailService.getEnergyFundDetail(energyId);
            return ApiUtils.success(result);
        }catch (NullPointerException e){
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}