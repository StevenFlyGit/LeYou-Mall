<template>
    <v-card>
        <v-card-title>
            <v-spacer>
                <v-btn color="primary" round >Primary</v-btn>
            </v-spacer>
            <v-text-field
                    v-model="search"
                    append-icon="search"
                    label="Search"
                    single-line
                    hide-details
            ></v-text-field>
        </v-card-title>
        <v-data-table
                :headers="headers"
                :items="desserts"
                :pagination.sync="pagination"
                :total-items="totalData"
                :loading="loading"
                class="elevation-1"
        >
            <template v-slot:items="props">
                <td class="text-xs-center">{{ props.item.id }}</td>
                <td class="text-xs-center">{{ props.item.name }}</td>
                <td class="text-xs-center">
                    <img v-if="props.item.image" :src="props.item.image" width="130" height="40">
                    <span v-else>无</span>
                </td>
                <td class="text-xs-center">{{ props.item.letter }}</td>
            </template>
            <template v-slot:no-results>
                <v-alert :value="true" color="error" icon="warning">
                    Your search for "{{ search }}" found no results.
                </v-alert>
            </template>
        </v-data-table>
    </v-card>
</template>

<script>
    export default {
        data () {
            return {
                search: '', //搜索关键字
                pagination: {}, // 分页信息
                totalData: 100,
                desserts: [],
                loading: true,
                headers: [
                    /*{
                        text: 'Dessert (100g serving)', //文本内容
                        align: 'left', //对齐方式
                        sortable: false, //是否进行排序
                        value: 'name' //该属性并没有实际作用
                    },*/
                    { text: '品牌编号', value: 'id', align: 'center' },
                    { text: '品牌名称', value: 'name' , align: 'center', sortable: false},
                    { text: '品牌logo', value: 'image', align: 'center', sortable: false},
                    { text: '品牌首字母', value: 'letter', align: 'center' }
                ] //表格头部数据
            }
        },
        mounted() {
            this.loadBrandData();
        },
        watch: {
            //通过监听内容的变化来重复执行异步函数(又事件驱动转为监听驱动)
            "search": { //监听简单变量，不需要设置深度监听
                handler: function () {
                    this.loadBrandData();
                }
            },
            "pagination": { //监听对象变量，需要设置深度监听
                handler: function () {
                    this.loadBrandData();
                }
            }
        },
        methods: {
            loadBrandData() {
                this.$http.get('/item/brand/page', {
                    params: {
                        key: this.search,
                        page: this.pagination.page,// 当前页
                        rows: this.pagination.rowsPerPage,// 每页大小
                        sortBy: this.pagination.sortBy,// 排序字段
                        desc: this.pagination.descending// 是否降序
                    }
                }).then(response => {
                    this.desserts = response.data.items;
                    this.totalData = response.data.total;
                    //将加载的进度条关闭
                    this.loading = false;
                }).catch(error => {
                    console.log(error);
                })
            }
        }
    }
</script>