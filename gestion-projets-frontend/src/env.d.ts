import { DefineComponent } from 'vue'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $t: (key: string, ...args: any[]) => string
    $tc: (key: string, choice?: number, ...args: any[]) => string
    $te: (key: string) => boolean
    $d: (value: Date | number, ...args: any[]) => string
    $n: (value: number, ...args: any[]) => string
  }
}

export {}
