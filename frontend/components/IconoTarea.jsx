import Image from "next/image";
import "@/styles/Componentes.css";

export default function IconoTarea({ icon, description }) {
    return (
        <div className="flex flex-col justify-center items-center">
            <div className="w-35 h-35 flex flex-col items-center justify-center shadow-md FondoIconoTarea">
                <Image src={icon} alt="Icon" className="w-30 h-30" />
            </div>
            <div className="w-4/5 text-center">
                <p className="TextoIconoTarea">
                    {description}
                </p>
            </div>
        </div>
    );
}
