

export const useResourceStore = defineStore('resourceStore', {
    state: () => ({
        /**
         * 资源列表
         */
        resources: [] as Resource[],
        /**
         * 已执行初始化
         */
        initialized: false,
    }),
    actions: {
        /**
         * 初始化资源列表
         */
        async initResources() {
            const res = await useSimpleFetch<Resource[]>('/gateway/portal/open/units', {
                method: 'POST',
                body: {}
            });
            this.resources = res.data || []
            this.initialized = true;
        },
        // 筛选出指定类型的资源
        async getResourceByType(type: string): Promise<Resource[]> {
            if (!this.initialized) {
                await this.initResources();
            }
            return this.resources.filter(resource => resource.type === type) || []
        }
    }
})
