package com.choice.cloud.architect.groot.dto.apollo;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * Apollo 分页查询应用的某个环境集群命名空间下的已更改历史配置信息响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/29 14:14
 */
@Data
public class ApolloPageQueryCommitItemHistoryListResponseDTO {

    /**
     * 对外输出结果
     */
    private String changeSets;
    private String appId;
    private String clusterName;
    private String namespaceName;
    private String dataChangeCreatedBy;
    private String dataChangeLastModifiedBy;
    private String dataChangeCreatedTime;
    private String dataChangeLastModifiedTime;

    /**
     * {
     *           "createItems":[
     *               {
     *                   "namespaceId":411,
     *                   "key":"lz.ke.soft2",
     *                   "value":"ds",
     *                   "lineNum":4,
     *                   "id":35415,
     *                   "isDeleted":false,
     *                   "dataChangeCreatedBy":"apollo",
     *                   "dataChangeCreatedTime":"2020-06-29 13:29:23",
     *                   "dataChangeLastModifiedBy":"apollo",
     *                   "dataChangeLastModifiedTime":"2020-06-29 13:29:23"
     *               }
     *           ],
     *           "updateItems":[
     *                {
     *                   "oldItem":{
     *                       "namespaceId":411,
     *                       "key":"din.yet",
     *                       "value":"dasdas",
     *                       "lineNum":2,
     *                       "id":34155,
     *                       "isDeleted":false,
     *                       "dataChangeCreatedBy":"apollo",
     *                       "dataChangeCreatedTime":"2020-06-16 17:11:55",
     *                       "dataChangeLastModifiedBy":"apollo",
     *                       "dataChangeLastModifiedTime":"2020-06-16 17:11:55"
     *                   },
     *                   "newItem":{
     *                       "namespaceId":411,
     *                       "key":"din.yet",
     *                       "value":"dasa",
     *                       "lineNum":2,
     *                       "id":34155,
     *                       "isDeleted":false,
     *                       "dataChangeCreatedBy":"apollo",
     *                       "dataChangeCreatedTime":"2020-06-16 17:11:55",
     *                       "dataChangeLastModifiedBy":"apollo",
     *                       "dataChangeLastModifiedTime":"2020-06-16 17:12:36"
     *                   }
     *               }
     *           ],
     *           "deleteItems":[
     *                {
     *                   "namespaceId":411,
     *                   "key":"test.key",
     *                   "value":"valuedesadas",
     *                   "lineNum":1,
     *                   "id":34125,
     *                   "isDeleted":true,
     *                   "dataChangeCreatedBy":"apollo",
     *                   "dataChangeCreatedTime":"2020-06-16 15:55:15",
     *                   "dataChangeLastModifiedBy":"apollo",
     *                   "dataChangeLastModifiedTime":"2020-06-16 16:37:30"
     *               }
     *           ]
     *      }
     */
    /**
     * 新增的配置项
     */
    private List<CreateItemsBean> createItems;
    /**
     * 修改的的配置项
     */
    private List<UpdateItemsBean> updateItems;
    /**
     * 删除的配置项
     */
    private List<DeleteItemsBean> deleteItems;

    @Data
    public static class CreateItemsBean {
        /**
         * namespaceId : 411
         * key : lz.ke.soft2
         * value : ds
         * lineNum : 4
         * id : 35415
         * isDeleted : false
         * dataChangeCreatedBy : apollo
         * dataChangeCreatedTime : 2020-06-29 13:29:23
         * dataChangeLastModifiedBy : apollo
         * dataChangeLastModifiedTime : 2020-06-29 13:29:23
         */
        private String namespaceId;
        private String key;
        private String value;
        private Integer lineNum;
        private Integer id;
        private Boolean isDeleted;
        private String dataChangeCreatedBy;
        private String dataChangeCreatedTime;
        private String dataChangeLastModifiedBy;
        private String dataChangeLastModifiedTime;
    }

    @Data
    public static class UpdateItemsBean {
        /**
         * oldItem : {"namespaceId":411,"key":"din.yet","value":"dasdas","lineNum":2,"id":34155,"isDeleted":false,"dataChangeCreatedBy":"apollo",
         * "dataChangeCreatedTime":"2020-06-16 17:11:55","dataChangeLastModifiedBy":"apollo","dataChangeLastModifiedTime":"2020-06-16 17:11:55"}
         * newItem : {"namespaceId":411,"key":"din.yet","value":"dasa","lineNum":2,"id":34155,"isDeleted":false,"dataChangeCreatedBy":"apollo",
         * "dataChangeCreatedTime":"2020-06-16 17:11:55","dataChangeLastModifiedBy":"apollo","dataChangeLastModifiedTime":"2020-06-16 17:12:36"}
         */
        private OldItemBean oldItem;
        private NewItemBean newItem;

        @Data
        public static class OldItemBean {
            /**
             * namespaceId : 411
             * key : din.yet
             * value : dasdas
             * lineNum : 2
             * id : 34155
             * isDeleted : false
             * dataChangeCreatedBy : apollo
             * dataChangeCreatedTime : 2020-06-16 17:11:55
             * dataChangeLastModifiedBy : apollo
             * dataChangeLastModifiedTime : 2020-06-16 17:11:55
             */
            private String namespaceId;
            private String key;
            private String value;
            private Integer lineNum;
            private Integer id;
            private Boolean isDeleted;
            private String dataChangeCreatedBy;
            private String dataChangeCreatedTime;
            private String dataChangeLastModifiedBy;
            private String dataChangeLastModifiedTime;
        }

        @Data
        public static class NewItemBean {
            /**
             * namespaceId : 411
             * key : din.yet
             * value : dasa
             * lineNum : 2
             * id : 34155
             * isDeleted : false
             * dataChangeCreatedBy : apollo
             * dataChangeCreatedTime : 2020-06-16 17:11:55
             * dataChangeLastModifiedBy : apollo
             * dataChangeLastModifiedTime : 2020-06-16 17:12:36
             */
            private String namespaceId;
            private String key;
            private String value;
            private Integer lineNum;
            private Integer id;
            private Boolean isDeleted;
            private String dataChangeCreatedBy;
            private String dataChangeCreatedTime;
            private String dataChangeLastModifiedBy;
            private String dataChangeLastModifiedTime;
        }
    }

    @Data
    public static class DeleteItemsBean {
        /**
         * namespaceId : 411
         * key : test.key
         * value : valuedesadas
         * lineNum : 1
         * id : 34125
         * isDeleted : true
         * dataChangeCreatedBy : apollo
         * dataChangeCreatedTime : 2020-06-16 15:55:15
         * dataChangeLastModifiedBy : apollo
         * dataChangeLastModifiedTime : 2020-06-16 16:37:30
         */
        private String namespaceId;
        private String key;
        private String value;
        private Integer lineNum;
        private Integer id;
        private Boolean isDeleted;
        private String dataChangeCreatedBy;
        private String dataChangeCreatedTime;
        private String dataChangeLastModifiedBy;
        private String dataChangeLastModifiedTime;
    }
}
