import {defineStore} from 'pinia';
import {useAuthStore} from './auth.ts';

interface CartItem {
    id: number;
    goodsName: string;
    newPrice: number;
    quantity: number;
}

export const useCartStore = defineStore('cart', {
    state: () => ({
        items: [] as CartItem[],
        selectedItems: [] as CartItem[],
    }),
    getters: {
        totalItems: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
        totalPrice: (state) => state.items.reduce((sum, item) => sum + item.newPrice * item.quantity, 0),
        userId(): number {
            const authStore = useAuthStore();
            return authStore.userInfo?.userId ?? -1;
        },
        cartKey(): string {
            return `cart_${this.userId}`;
        },
    },
    actions: {
        loadCart() {
            const authStore = useAuthStore();
            const userId = authStore.userInfo?.userId ?? -1;
            const newKey = `cart_${userId}`;
            const oldKey = 'cart';

            let saved = localStorage.getItem(newKey);
            if (!saved) {
                const oldSaved = localStorage.getItem(oldKey);
                if (oldSaved) {
                    localStorage.setItem(newKey, oldSaved);
                    localStorage.removeItem(oldKey);
                    saved = oldSaved;
                }
            }

            this.items = saved ? JSON.parse(saved) as CartItem[] : [];
        },
        saveCart() {
            const authStore = useAuthStore();
            const userId = authStore.userInfo?.userId ?? -1;
            const key = `cart_${userId}`;
            localStorage.setItem(key, JSON.stringify(this.items));
        },
        addItem(item: Omit<CartItem, 'quantity'>) {
            const existing = this.items.find((i) => i.id === item.id);
            if (existing) {
                existing.quantity += 1;
            } else {
                this.items.push({...item, quantity: 1});
            }
            this.saveCart();
        },
        updateQuantity(item: CartItem) {
            const index = this.items.findIndex((i) => i.id === item.id);
            if (index !== -1) {
                this.items[index].quantity = item.quantity;
                this.saveCart();
            }
        },
        removeItem(id: number) {
            this.items = this.items.filter((item) => item.id !== id);
            this.saveCart();
        },
        setSelectedItems(selectedItems: CartItem[]) {
            this.selectedItems = selectedItems;
        },
    },
});
