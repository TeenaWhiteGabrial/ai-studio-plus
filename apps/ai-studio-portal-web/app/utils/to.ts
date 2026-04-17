/** 转换类工具函数集合 */


/**
 * 将 HEX 颜色转换为 HSLA
 */
export function hexToHsla(hex: string): [number, number, number, number] {
    const r = parseInt(hex.slice(1, 3), 16) / 255
    const g = parseInt(hex.slice(3, 5), 16) / 255
    const b = parseInt(hex.slice(5, 7), 16) / 255
    const a = hex.length === 9 ? parseInt(hex.slice(7, 9), 16) / 255 : 1 // 支持8位HEX

    const max = Math.max(r, g, b)
    const min = Math.min(r, g, b)
    let h = 0
    let s = 0
    const l = (max + min) / 2

    if (max !== min) {
        const d = max - min
        s = l > 0.5 ? d / (2 - max - min) : d / (max + min)

        switch (max) {
            case r: h = (g - b) / d + (g < b ? 6 : 0); break
            case g: h = (b - r) / d + 2; break
            case b: h = (r - g) / d + 4; break
        }
        h /= 6
    }

    return [Math.round(h * 360), Math.round(s * 100), Math.round(l * 100), a]
}

/**
 * 将 HSLA 颜色转换为 HEX
 */
export function hslaToHex(h: number, s: number, l: number, a: number = 1): string {
    h = h % 360
    s = Math.max(0, Math.min(100, s)) / 100
    l = Math.max(0, Math.min(100, l)) / 100
    a = Math.max(0, Math.min(1, a))

    const c = (1 - Math.abs(2 * l - 1)) * s
    const x = c * (1 - Math.abs((h / 60) % 2 - 1))
    const m = l - c / 2

    let r = 0, g = 0, b = 0

    if (0 <= h && h < 60) {
        r = c; g = x; b = 0
    } else if (60 <= h && h < 120) {
        r = x; g = c; b = 0
    } else if (120 <= h && h < 180) {
        r = 0; g = c; b = x
    } else if (180 <= h && h < 240) {
        r = 0; g = x; b = c
    } else if (240 <= h && h < 300) {
        r = x; g = 0; b = c
    } else if (300 <= h && h < 360) {
        r = c; g = 0; b = x
    }

    r = Math.round((r + m) * 255)
    g = Math.round((g + m) * 255)
    b = Math.round((b + m) * 255)
    const alpha = Math.round(a * 255)

    // 如果透明度为1，返回6位HEX，否则返回8位HEX
    if (a === 1) {
        return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`
    } else {
        return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}${alpha.toString(16).padStart(2, '0')}`
    }
}