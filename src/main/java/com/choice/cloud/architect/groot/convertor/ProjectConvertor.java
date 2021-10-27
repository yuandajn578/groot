package com.choice.cloud.architect.groot.convertor;

import java.util.List;

import com.choice.cloud.architect.groot.dto.project.InfoProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectResponseDTO;
import com.choice.cloud.architect.groot.model.TGrProject;
import org.mapstruct.Mapper;

/**
 * <p>
 * 项目对象转换器
 * </p>
 *
 * @author LZ
 */
@Mapper(componentModel = "spring")
public interface ProjectConvertor {
    List<PageListProjectResponseDTO> pageListDo2Response(List<TGrProject> projectList);

    InfoProjectResponseDTO do2Response(TGrProject project);
}
