<template>
    <el-cascader 
        @change="addressChange" 
        v-model='selectedOptions'
        :options="regionDataCorrection"
        :props="props" 
        clearable
    ></el-cascader>
</template>
<script setup>
    import { regionData } from 'element-china-area-data'
    
    // 定义自定义事件，用于向父组件传递地址数据
    const emit = defineEmits(['address-change'])

    const userAddresses = ref([])
    let selectedOptions = []
    
    const { multiple, addChinaZone,defaultAreaCode } = defineProps({
        multiple: {
            type: Boolean,
            default: false
        },
        // 全国区域
        addChinaZone: {
            type: Boolean,
            default: false
        },
        // 初始化选中的区域code
        defaultAreaCode: {
            type: Array,
            default: () => []
        }
    })
    const props = {
        expandTrigger: 'hover',
        multiple,
        collapseTags: true, // 折叠多选标签
        maxCollapseTags: 2, // 最多显示的折叠标签数量
        collapseTagsTooltip: true, // 折叠标签时显示tooltip
        checkStrictly: multiple, // 多选支持选择任意级别的节点，单选不支持
    }

    let regionDataCorrection = []
    
    if (multiple && addChinaZone) {
        regionDataCorrection = [{
            value: 'all',
            label: '全国',
            children: regionData
        }]
    } else {
        regionDataCorrection = regionData
    }

    function addressChange(arr) {
        if (!arr || !Array.isArray(arr)) return
        // 多选区域逻辑处理
        if(multiple){
            // 没有全国选项
            if(!addChinaZone){
                // 提取选中的区域code数组
                const selectedCodes = arr.map(region => {
                    if (region.length === 3 && region[2]) {
                        // 选中了三级地区（区县）
                        return region[2]
                    } else if (region.length === 2 && region[1]) {
                        // 选中了二级地区（城市）
                        return `${region[1]}00`
                    } else if (region.length === 1 && region[0]) {
                        // 选中了一级地区（省份）
                        return `${region[0]}0000`
                    }
                    return ''
                }).filter(code => code !== '')

                // 更新用户地址数组（保留原有功能，方便其他地方使用）
                userAddresses.value = arr.map(region => ({
                    provinceCode: region[0],
                    cityCode: region[1],
                    countyCode: region[2],
                    fullLocation: selectedCodes
                }))
                // 触发自定义事件，传递选中的地区代码数组
                emit('address-change', selectedCodes)
            } else {
                // 有全国选项
                // 提取选中的区域code数组
                const selectedCodes = arr.map(region => {
                    if (region.length === 4 && region[3]) {
                        // 选中了四级地区（区县）
                        return region[2]
                    } else if (region.length === 3 && region[2]) {
                        // 选中了三级地区（城市）
                        return `${region[2]}00`
                    } else if (region.length === 2 && region[1]) {
                        // 选中了二级地区（省份）
                        return `${region[1]}0000`
                    } else if (region.length === 1 && region[0]){
                        // 选中了全国选项
                        return 'all'
                    } else {
                        return ''
                    }
                }).filter(code => code !== '')

                // 更新用户地址数组（保留原有功能，方便其他地方使用）
                userAddresses.value = arr.map(region => ({
                    provinceCode: region[1],
                    cityCode: region[2],
                    countyCode: region[3],
                    fullLocation: selectedCodes
                }))
                // 触发自定义事件，传递选中的地区代码数组
                emit('address-change', selectedCodes.includes('all')?['all']:selectedCodes)
            }            
        } else {
            const selectedCode = arr[arr.length - 1]
            const newCode = `${selectedCode}0000`.substring(0,6)
            emit('address-change', newCode)
        }        
    }

    /**
     * 将6位地区代码转换为层级数组格式
     * @param code 6位地区代码字符串
     * @returns 层级数组，如['11', '1101', '110101']，00后缀会被自动去除
     */
    function convertAreaCodeToLevels(code) {
        const provinceCode = code.substring(0, 2)
        const cityCode = code.substring(0, 4)
        const countyCode = code.substring(0, 6)
        
        if(code === 'all'){
            return 'all'
        }
        let res = []
        // 根据代码后缀是否为00来决定返回的层级结构
        if (countyCode.endsWith('0000')) {
            // 如果最后四位是0000，说明是省级代码，只返回省份部分
            res = [provinceCode]
        } else if (countyCode.endsWith('00')) {
            // 如果最后两位是00，说明是市级代码，返回省份和城市部分
            res = [provinceCode, cityCode]
        } else {
            // 否则是县级代码，返回完整的三级结构
            res = [provinceCode, cityCode, countyCode]
        }
        if(addChinaZone){
            return ['all', ...res]
        } else {
            return res
        }
    }

    onMounted(() => {
        // 单选
        if (!multiple && defaultAreaCode.length === 1) {
            selectedOptions = convertAreaCodeToLevels(defaultAreaCode[0].substring(0, 6))            
        }
        if(multiple){
            selectedOptions = defaultAreaCode.map(code => convertAreaCodeToLevels(code.substring(0, 6)))
        }
    })
</script>