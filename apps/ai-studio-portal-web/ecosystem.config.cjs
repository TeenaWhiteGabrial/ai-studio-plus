module.exports = {
    apps: [
        {
            name: 'ai-studio-portal-web',
            port: '3000',
            // 优化执行模式：根据 CPU 核心数智能分配
            exec_mode: 'fork',
            // instances: 'max', // 根据 CPU 核心数自动创建实例
            instances: 1, // 固定实例数
            script: './.output/server/index.mjs',
            
            // 内存限制：防止单个实例占用过多内存
            max_memory_restart: '1G', // 单个实例内存超过 1GB 时自动重启

            // 日志配置
            error_file: './logs/pm2-error.log',
            out_file: './logs/pm2-out.log',
            log_date_format: 'YYYY-MM-DD HH:mm:ss Z',
            merge_logs: true,
            
            // 自动重启配置
            watch: false, // 生产环境禁用文件监听
            ignore_watch: ['node_modules', 'logs', '.nuxt', '.output'],
            
            // 进程管理
            min_uptime: '10s', // 进程运行至少 10 秒才算稳定
            max_restarts: 10, // 最大重启次数
            restart_delay: 4000, // 重启延迟（毫秒）
            
            // 性能监控
            pmx: true, // 启用 PM2 Plus 监控（可选）
            
            // 实例间负载均衡优化
            instance_var: 'INSTANCE_ID', // 实例 ID 环境变量
        },
    ],
}
