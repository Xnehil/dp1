// On pressing Connect this method will be called 
export function conectarAWebsocket(): WebSocket { 
        const url_base: string | undefined = process.env.REACT_APP_WS_URL_BASE;
        let ws = new WebSocket(url_base+"/socket" ?? '');

        // This function will called every time a new message arrives 
        ws.onmessage = (e: MessageEvent) => { 
                console.log(e); 
        }; 
        return ws;
} 

interface MessageData {
    name: string;
    message: string;
}

export function enviarMensaje(mensaje: string, currentUser: string, ws: WebSocket | null) {
    if (ws) {
        const data: MessageData = {
            name: currentUser || "Anónimo",
            message: mensaje,
        };
        ws.send(JSON.stringify(data));
    }
}
