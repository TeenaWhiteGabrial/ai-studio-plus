<template>
  <div class="component-upload-image">
    <el-upload
      multiple
      :disabled="disabled"
      :action="uploadImgUrl"
      list-type="picture-card"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :http-request="handleUpload"
      :data="data"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      ref="imageUpload"
      :before-remove="handleDelete"
      :show-file-list="true"
      :file-list="fileList"
      :on-preview="handlePictureCardPreview"
      :class="{ hide: fileList.length >= limit }"
    >
      <Icon name="material-symbols:upload" size="60" />
    </el-upload>
    <!-- 上传提示 -->
    <div class="el-upload__tip" v-if="showTip && !disabled">
      请上传
      <template v-if="fileSize">
        大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
      </template>
      <template v-if="fileType">
        格式为 <b style="color: #f56c6c">{{ fileType.join("/") }}</b>
      </template>
      的图片
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="预览"
      width="800px"
      append-to-body
    >
      <img
        :src="dialogImageUrl"
        style="display: block; max-width: 100%; margin: 0 auto"
      />
    </el-dialog>
  </div>
</template>

<script  setup>
    import Sortable from 'sortablejs'

    const config = useRuntimeConfig()

    const props = defineProps({
        modelValue: [String, Object, Array],
        // 上传携带的参数
        data: {
            type: Object
        },
        // 图片数量限制
        limit: {
            type: Number,
            default: 5
        },
        // 大小限制(MB)
        fileSize: {
            type: Number,
            default: 5
        },
        // 图片类型, 例如['png', 'jpg', 'jpeg']
        fileType: {
            type: Array,
            default: () => ["png", "jpg", "jpeg"]
        },
        // 是否显示提示
        isShowTip: {
            type: Boolean,
            default: true
        },
        // 禁用组件（仅查看图片）
        disabled: {
            type: Boolean,
            default: false
        },
        // 拖动排序
        drag: {
            type: Boolean,
            default: true
        }
    })

    const { proxy } = getCurrentInstance()
    const emit = defineEmits()
    const number = ref(0)
    const uploadList = ref([])
    const dialogImageUrl = ref("")
    const dialogVisible = ref(false)
    const baseUrl = config.public.apiBase
    // 虽然使用了自定义上传http-request，但action属性仍需一个非空值
    const uploadImgUrl = ref('#') // 使用#作为占位符
    const fileList = ref([])
    const showTip = computed(
        () => props.isShowTip && (props.fileType || props.fileSize)
    )

    watch(() => props.modelValue, val => {
        if (val) {
            // 首先将值转为数组
            const list = Array.isArray(val) ? val : props.modelValue.split(",")
            // 然后将数组转为对象数组
            fileList.value = list.map(item => {
                if (typeof item === "string") {
                    if (item.indexOf(baseUrl) === -1 && !isExternalUrl(item)) {
                        item = { name: baseUrl + item, url: baseUrl + item }
                    } else {
                        item = { name: item, url: item }
                    }
                }
                return item
            })
        } else {
            fileList.value = []
            return []
        }
    }, { deep: true, immediate: true })

    // 上传前loading加载
    function handleBeforeUpload(file) {
        let isImg = false
        if (props.fileType.length) {
            let fileExtension = ""
            if (file.name.lastIndexOf(".") > -1) {
                fileExtension = file.name.slice(file.name.lastIndexOf(".") + 1)
            }
            isImg = props.fileType.some(type => {
                if (file.type.indexOf(type) > -1) return true
                if (fileExtension && fileExtension.indexOf(type) > -1) return true
                return false
            })
        } else {
            isImg = file.type.indexOf("image") > -1
        }
        if (!isImg) {
            ElMessage.error(`图片格式不正确，请上传${props.fileType.join("/")}图片格式图片!`)
            return false
        }
        if (file.name.includes(',')) {
            ElMessage.error('图片名不正确，不能包含英文逗号!')
            return false
        }
        if (props.fileSize) {
            const isLt = file.size / 1024 / 1024 < props.fileSize
            if (!isLt) {
                ElMessage.error(`上传图片大小不能超过 ${props.fileSize} MB!`)   
                return false
            }
        }
        number.value++
        // 必须返回true才能继续上传流程
        return true
    }

    // 图片个数超出
    function handleExceed() {
        ElMessage.error(`上传图片数量不能超过 ${props.limit} 个!`)
    }

    // 上传成功回调
    function handleUploadSuccess({fileName,fileUrl}) {
        if (fileUrl) {
            uploadList.value.push({ name: fileName, url: fileUrl })
            uploadedSuccessfully()
        } else {
            number.value--
            ElMessage.error(res.msg)
            uploadedSuccessfully()
        }
    }

    // 删除图片
    function handleDelete(file) {
        const findex = fileList.value.map(f => f.name).indexOf(file.name)
        if (findex > -1 && uploadList.value.length === number.value) {
            fileList.value.splice(findex, 1)
            emit("update:modelValue", listToString(fileList.value))
            return false
        }
    }

    // 上传结束处理
    function uploadedSuccessfully() {
        if (number.value > 0 && uploadList.value.length === number.value) {
            fileList.value = fileList.value.filter(f => f.url !== undefined).concat(uploadList.value)
            uploadList.value = []
            number.value = 0
            emit("update:modelValue", listToString(fileList.value))
            ElMessage.success("上传图片成功")
        }
    }

    // 上传失败
    function handleUploadError() {
        ElMessage.error("上传图片失败")
    }

    // 预览
    function handlePictureCardPreview(file) {
        dialogImageUrl.value = file.url
        dialogVisible.value = true
    }

    // 对象转成指定字符串分隔
    function listToString(list, separator) {
        let strs = ""
        separator = separator || ","
        for (let i in list) {
            if (undefined !== list[i].url && list[i].url.indexOf("blob:") !== 0) {
                strs += list[i].url.replace(baseUrl, "") + separator
            }
        }
        return strs != "" ? strs.substr(0, strs.length - 1) : ""
    }

    // 自定义上传
    async function handleUpload(param){
        const formData = new FormData()
        if (param && param.file) {
            formData.append('file', param.file)
        }
        const { success, msg, data } = await useSimpleFetch('/file/uploadFile',
        {
            method:'post',
            body: formData
        })
        
        if (success === 'true') {
            param.onSuccess({ fileName: data.data.fileName,fileUrl: data.url })
        } else {
            param.onError({ msg: msg })
        }
    }
    // 初始化拖拽排序
    onMounted(() => {
        if (props.drag && !props.disabled) {
            nextTick(() => {
                const element = proxy.$refs.imageUpload?.$el?.querySelector('.el-upload-list')
                Sortable.create(element, {
                    onEnd: (evt) => {
                        const movedItem = fileList.value.splice(evt.oldIndex, 1)[0]
                        fileList.value.splice(evt.newIndex, 0, movedItem)
                        emit('update:modelValue', listToString(fileList.value))
                    }
                })
            })
        }
    })
</script>

<style scoped >
    :deep(.hide .el-upload--picture-card) {
        display: none;
    }

    :deep(.el-upload.el-upload--picture-card.is-disabled) {
        display: none !important;
    }
</style>
