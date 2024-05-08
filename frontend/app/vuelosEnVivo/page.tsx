"use client";

import React, { useEffect, useRef } from "react";
import "ol/ol.css";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import Mapa from "@/components/mapa/Mapa";

const Page = () => {

  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, []);

  return (
        <div className="pb-4">
            <Mapa />
            <div ref={bottomRef}></div>
        </div>
    );
};

export default Page;
